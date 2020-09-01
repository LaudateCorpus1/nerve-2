package io.nuls.api.db.mongo;

import com.mongodb.client.model.*;
import io.nuls.api.ApiContext;
import io.nuls.api.cache.ApiCache;
import io.nuls.api.constant.ApiConstant;
import io.nuls.api.constant.DBTableConstant;
import io.nuls.api.db.AccountLedgerService;
import io.nuls.api.db.SymbolRegService;
import io.nuls.api.manager.CacheManager;
import io.nuls.api.model.dto.Asset;
import io.nuls.api.model.po.AccountLedgerInfo;
import io.nuls.api.model.po.AssetInfo;
import io.nuls.api.model.po.SymbolRegInfo;
import io.nuls.api.utils.DocumentTransferTool;
import io.nuls.core.constant.CommonCodeConstanst;
import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.exception.NulsRuntimeException;
import io.nuls.core.log.Log;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.checkerframework.checker.units.qual.A;

import java.math.BigInteger;
import java.util.*;

import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.ne;

@Component
public class MongoAccountLedgerServiceImpl implements AccountLedgerService {

    @Autowired
    private MongoDBService mongoDBService;

    private List<String> keyList = new LinkedList<>();

    private static int cacheSize = 30000;

    @Autowired
    SymbolRegService symbolRegService;

    @Override
    public void initCache() {
        for (ApiCache apiCache : CacheManager.getApiCaches().values()) {
            List<Document> documentList = mongoDBService.pageQuery(DBTableConstant.ACCOUNT_LEDGER_TABLE + apiCache.getChainInfo().getChainId(), 0, cacheSize);
            for (Document document : documentList) {
                AccountLedgerInfo ledgerInfo = DocumentTransferTool.toInfo(document, "key", AccountLedgerInfo.class);
                apiCache.addAccountLedgerInfo(ledgerInfo);
                keyList.add(ledgerInfo.getKey());
            }
        }
    }

    @Override
    public AccountLedgerInfo getAccountLedgerInfo(int chainId, String key) {
        ApiCache apiCache = CacheManager.getCache(chainId);
        AccountLedgerInfo accountLedgerInfo = apiCache.getAccountLedgerInfo(key);
        if (accountLedgerInfo == null) {
            Document document = mongoDBService.findOne(DBTableConstant.ACCOUNT_LEDGER_TABLE + chainId, Filters.eq("_id", key));
            if (document == null) {
                return null;
            }
            accountLedgerInfo = DocumentTransferTool.toInfo(document, "key", AccountLedgerInfo.class);
            while (keyList.size() >= cacheSize) {
                key = keyList.remove(0);
                apiCache.getLedgerMap().remove(key);
            }
            apiCache.addAccountLedgerInfo(accountLedgerInfo);
            keyList.add(accountLedgerInfo.getKey());
        }
        return accountLedgerInfo.copy();
    }

    @Override
    public Map<String, Long> aggAssetAddressCount(int chainId){
        Bson id = new Document().append("chainId" , "$chainId").append("assetId","$assetId");
        Bson count = new Document().append("$sum",1);
        Bson group = new Document("$group",new Document().append("_id",id).append("count",count));
        return mongoDBService.aggReturnDoc(DBTableConstant.ACCOUNT_LEDGER_TABLE + chainId,group)
        .stream().map(d->{
            Document doc = (Document) d.get("_id");
            String assetChainId = doc.get("chainId").toString();
            String assetId = doc.get("assetId").toString();
            return Map.of(assetChainId + ApiConstant.SPACE + assetId,Long.parseLong(d.get("count").toString()));
        }).reduce(new HashMap<>(),(d1,d2)->{
            d1.putAll(d2);
            return d1;
        });
    }

    @Override
    public Map<String, BigInteger> aggAssetBalanceTotal(int chainId) {
        Bson id = new Document().append("chainId" , "$chainId").append("assetId","$assetId");
        Bson total = new Document().append("$sum","$totalBalance");
        Bson match = Aggregates.match(ne("address", ApiContext.blackHoleAddress));
        Bson group = new Document("$group",new Document().append("_id",id).append("total",total));
        return mongoDBService.aggReturnDoc(DBTableConstant.ACCOUNT_LEDGER_TABLE + chainId,match,group)
                .stream().map(d->{
                    Document doc = (Document) d.get("_id");
                    String assetChainId = doc.get("chainId").toString();
                    String assetId = doc.get("assetId").toString();
                    return Map.of(assetChainId + ApiConstant.SPACE + assetId,new BigInteger(d.get("total").toString()));
                }).reduce(new HashMap<>(),(d1,d2)->{
                    d1.putAll(d2);
                    return d1;
                });
    }

    @Override
    public void saveLedgerList(int chainId, Map<String, AccountLedgerInfo> accountLedgerInfoMap) {
        if (accountLedgerInfoMap.isEmpty()) {
            return;
        }
        BulkWriteOptions options = new BulkWriteOptions();
        options.ordered(false);
        List<WriteModel<Document>> modelList = new ArrayList<>();
        int i = 0;
        for (AccountLedgerInfo ledgerInfo : accountLedgerInfoMap.values()) {
            if(ledgerInfo.getTotalBalance().compareTo(BigInteger.ZERO) < 0){
                throw new NulsRuntimeException(CommonCodeConstanst.DATA_ERROR,"数据异常，资产不能为负: " + ledgerInfo.getKey());
            }
            Document document = DocumentTransferTool.toDocument(ledgerInfo, "key");
            if (ledgerInfo.isNew()) {
                modelList.add(new InsertOneModel(document));
                ledgerInfo.setNew(false);
            } else {
                modelList.add(new ReplaceOneModel<>(Filters.eq("_id", ledgerInfo.getKey()), document));
            }
            i++;
            if (i == 1000) {
                mongoDBService.bulkWrite(DBTableConstant.ACCOUNT_LEDGER_TABLE + chainId, modelList, options);
                modelList.clear();
                i = 0;
            }
        }
        if (modelList.size() > 0) {
            mongoDBService.bulkWrite(DBTableConstant.ACCOUNT_LEDGER_TABLE + chainId, modelList, options);
        }

        ApiCache apiCache = CacheManager.getCache(chainId);
        for (AccountLedgerInfo ledgerInfo : accountLedgerInfoMap.values()) {
            if (apiCache.getLedgerMap().containsKey(ledgerInfo.getKey())) {
                apiCache.addAccountLedgerInfo(ledgerInfo);
            }
        }
    }

    @Override
    public List<AccountLedgerInfo> getAccountLedgerInfoList(int chainId, String address) {
        Bson filter = Filters.eq("address", address);
        List<Document> documentList = mongoDBService.query(DBTableConstant.ACCOUNT_LEDGER_TABLE + chainId, filter);
        List<AccountLedgerInfo> accountLedgerInfoList = new ArrayList<>();
        for (Document document : documentList) {
            if (document.getInteger("chainId") != chainId) {
                continue;
            }
            AccountLedgerInfo ledgerInfo = DocumentTransferTool.toInfo(document, "key", AccountLedgerInfo.class);
            SymbolRegInfo symbolRegInfo = symbolRegService.get(ledgerInfo.getChainId(),ledgerInfo.getAssetId());
            if(symbolRegInfo.getSource() == 1){
                ledgerInfo.setSource(symbolRegInfo.getSource());
                accountLedgerInfoList.add(ledgerInfo);
            }
        }
        if (accountLedgerInfoList.isEmpty()) {
            AssetInfo assetInfo = CacheManager.getCacheChain(chainId).getDefaultAsset();
            AccountLedgerInfo accountLedgerInfo = new AccountLedgerInfo(address, assetInfo.getChainId(), assetInfo.getAssetId());
            accountLedgerInfoList.add(accountLedgerInfo);
        }
        return accountLedgerInfoList;
    }

    @Override
    public List<AccountLedgerInfo> getAccountCrossLedgerInfoList(int chainId, String address) {
        Bson filter = Filters.eq("address", address);
        List<Document> documentList = mongoDBService.query(DBTableConstant.ACCOUNT_LEDGER_TABLE + chainId, filter);
        List<AccountLedgerInfo> accountLedgerInfoList = new ArrayList<>();

        for (Document document : documentList) {
            AccountLedgerInfo ledgerInfo = DocumentTransferTool.toInfo(document, "key", AccountLedgerInfo.class);
            SymbolRegInfo symbolRegInfo = symbolRegService.get(ledgerInfo.getChainId(),ledgerInfo.getAssetId());
            if(symbolRegInfo.getSource() > 1){
                ledgerInfo.setSource(symbolRegInfo.getSource());
                accountLedgerInfoList.add(ledgerInfo);
            }
        }
        return accountLedgerInfoList;
    }

    @Override
    public List<Document> getAllBalance(int chainId){
        return mongoDBService.query(DBTableConstant.ACCOUNT_LEDGER_TABLE + chainId);
    }

}
