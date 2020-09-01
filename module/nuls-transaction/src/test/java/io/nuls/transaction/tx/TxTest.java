/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.nuls.transaction.tx;

import io.nuls.base.RPCUtil;
import io.nuls.base.basic.AddressTool;
import io.nuls.base.basic.NulsByteBuffer;
import io.nuls.base.data.Transaction;
import io.nuls.base.signture.P2PHKSignature;
import io.nuls.base.signture.TransactionSignature;
import io.nuls.core.constant.TxType;
import io.nuls.core.log.Log;
import io.nuls.core.rpc.info.Constants;
import io.nuls.core.rpc.info.HostInfo;
import io.nuls.core.rpc.info.NoUse;
import io.nuls.core.rpc.model.ModuleE;
import io.nuls.core.rpc.netty.processor.ResponseMessageProcessor;
import io.nuls.transaction.constant.TxConstant;
import io.nuls.transaction.model.bo.Chain;
import io.nuls.transaction.model.bo.config.ConfigBean;
import io.nuls.transaction.model.dto.CoinDTO;
import io.nuls.transaction.rpc.call.TransactionCall;
import io.nuls.transaction.txdata.TradingOrder;
import io.nuls.transaction.utils.TxUtil;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试交易
 * @author: Charlie
 * @date: 2020/6/12
 */
public class TxTest {

    static String address20 = "TNVTdTSPVcqUCdfVYWwrbuRtZ1oM6GpSgsgF5";
    static String address21 = "TNVTdTSPNEpLq2wnbsBcD8UDTVMsArtkfxWgz";
    static String address22 = "TNVTdTSPRyJgExG4HQu5g1sVxhVVFcpCa6fqw";
    static String address23 = "TNVTdTSPUR5vYdstWDHfn5P8MtHB6iZZw3Edv";
    static String address24 = "TNVTdTSPPXtSg6i5sPPrSg3TfFrhYHX5JvMnD";
    static String address25 = "TNVTdTSPT5KdmW1RLzRZCa5yc7sQCznp6fES5";
    static String address26 = "TNVTdTSPPBao2pGRc5at7mSdBqnypJbMqrKMg";
    static String address27 = "TNVTdTSPLqKoNh2uiLAVB76Jyq3D6h3oAR22n";
    static String address28 = "TNVTdTSPNkjaFbabm5P73m7VHBRQef4NDsgYu";
    static String address29 = "TNVTdTSPRMtpGNYRx98WkoqKnExU9pWDQjNPf";
    static String address30 = "TNVTdTSPEn3kK94RqiMffiKkXTQ2anRwhN1J9";
    static String address31 = "TNVTdTSPRyiWcpbS65NmT5qyGmuqPxuKv8SF4";

    private Chain chain;
    static int chainId = 5;
    static int assetId = 1;
    static int heterogeneousChainId = 101;
    static int heterogeneousAssetId = 1;

    static String version = "1.0";

    static String password = "nuls123456";//"nuls123456";

    @Before
    public void before() throws Exception {
        NoUse.mockModule();
        ResponseMessageProcessor.syncKernel("ws://" + HostInfo.getLocalIP() + ":7771");
        chain = new Chain();
        chain.setConfig(new ConfigBean(chainId, assetId));
    }


    @Test
    public void createWrongTx() throws Exception {
        Map map = CreateTx.createTransferTx(address21, address25, new BigInteger("1000000000"));
        Transaction tx = CreateTx.assemblyTransaction((List<CoinDTO>) map.get("inputs"), (List<CoinDTO>) map.get("outputs"), (String) map.get("remark"), null, 1591947442L, TxType.QUOTATION);
        sign(tx, address31, password);
        Log.info("{}", tx.format());
        newTx(tx);
    }
    @Test
    public void createTx() throws Exception {
        for (int i = 0; i < 1; i++) {
            Map map = CreateTx.createTransferTx(address20, address25, new BigInteger("2200000000"));
            Transaction tx = CreateTx.assemblyTransaction((List<CoinDTO>) map.get("inputs"), (List<CoinDTO>) map.get("outputs"), (String) map.get("remark"), null, 1593070691L, TxType.TRANSFER);
            Log.info("{}", tx.format());
            newTx(tx);
        }
    }

    private void newTx(Transaction tx) throws Exception {
        Map<String, Object> params = new HashMap<>(TxConstant.INIT_CAPACITY_8);
        params.put(Constants.VERSION_KEY_STR, TxConstant.RPC_VERSION);
        params.put(Constants.CHAIN_ID, chainId);
        params.put("tx", RPCUtil.encode(tx.serialize()));
        HashMap result = (HashMap) TransactionCall.requestAndResponse(ModuleE.TX.abbr, "tx_newTx", params);
//        System.out.println(result.get("hash"));
    }

    /**
     * 对交易hash签名(在线)
     * @param tx
     * @param address
     * @param password
     */
    public void sign(Transaction tx, String address, String password) throws Exception {
        TransactionSignature transactionSignature = new TransactionSignature();
        List<P2PHKSignature> p2PHKSignatures = new ArrayList<>();
        Map<String, Object> params = new HashMap<>(TxConstant.INIT_CAPACITY_8);
        params.put(Constants.VERSION_KEY_STR, TxConstant.RPC_VERSION);
        params.put(Constants.CHAIN_ID, chainId);
        params.put("address", address);
        params.put("password", password);
        params.put("data", RPCUtil.encode(tx.getHash().getBytes()));
        HashMap result = (HashMap) TransactionCall.requestAndResponse(ModuleE.AC.abbr, "ac_signDigest", params);
        String signatureStr = (String) result.get("signature");
        P2PHKSignature signature = new P2PHKSignature();
        signature.parse(new NulsByteBuffer(RPCUtil.decode(signatureStr)));
        p2PHKSignatures.add(signature);
        //交易签名
        transactionSignature.setP2PHKSignatures(p2PHKSignatures);
        tx.setTransactionSignature(transactionSignature.serialize());
    }

    public static void main(String[] args) throws Exception {
        AddressTool.addPrefix(5, "TNVT");
        String hex = "e5003e7fe85e00911ef715cec29207455a4f2c9b58864cc3797d5446a5574bad0d1c496ef9c19b07050001f88d93a52edc7437da5e2977d27681f0fb1e6bab0200e1f5050000000000000000000000000000000000000000000000000000000000e1f505000000000000000000000000000000000000000000000000000000001704000102dc3a715ee3d1faa7f81cdea0687292d40c189d058c0117050001f88d93a52edc7437da5e2977d27681f0fb1e6bab05000100a067f7050000000000000000000000000000000000000000000000000000000008bac1b7d8e684c077000117050001f88d93a52edc7437da5e2977d27681f0fb1e6bab0500010000e1f50500000000000000000000000000000000000000000000000000000000feffffffffffffff6a21025ffc3303fdf0e432b46c37a9c18e5e7feef00d68fef70029399c17dca34f117d473045022076d0cb8c87ca2a498d0fa64a0d77e5f0f0c037b0c5353183ac2e9eba7e55da95022100d9ab33b0ffd33c63a72c4026cfe50dbe3b3d2c9183cdd026cf5bdffb4f748f45";
        Transaction tx = TxUtil.getInstance(hex, Transaction.class);
        String format = tx.format(TradingOrder.class);
        System.out.println(format);

    }
}
