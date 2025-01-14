package network.nerve.converter.heterogeneouschain.trx.core;

import com.google.protobuf.ByteString;
import com.google.protobuf.UnknownFieldSet;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.nuls.core.exception.NulsException;
import io.nuls.core.log.logback.NulsLogger;
import io.nuls.core.model.StringUtils;
import network.nerve.converter.heterogeneouschain.lib.context.HtgConstant;
import network.nerve.converter.heterogeneouschain.lib.context.HtgContext;
import network.nerve.converter.heterogeneouschain.lib.core.ExceptionFunction;
import network.nerve.converter.heterogeneouschain.lib.management.BeanInitial;
import network.nerve.converter.heterogeneouschain.trx.constant.TrxConstant;
import network.nerve.converter.heterogeneouschain.trx.model.TrxEstimateSun;
import network.nerve.converter.heterogeneouschain.trx.model.TrxSendTransactionPo;
import network.nerve.converter.heterogeneouschain.trx.utils.TrxUtil;
import network.nerve.converter.utils.ConverterUtil;
import org.tron.trident.abi.FunctionEncoder;
import org.tron.trident.abi.FunctionReturnDecoder;
import org.tron.trident.abi.TypeReference;
import org.tron.trident.abi.datatypes.Address;
import org.tron.trident.abi.datatypes.Function;
import org.tron.trident.abi.datatypes.Type;
import org.tron.trident.abi.datatypes.generated.Uint256;
import org.tron.trident.api.GrpcAPI;
import org.tron.trident.core.ApiWrapper;
import org.tron.trident.core.key.KeyPair;
import org.tron.trident.core.transaction.TransactionBuilder;
import org.tron.trident.proto.Chain;
import org.tron.trident.proto.Contract;
import org.tron.trident.proto.Response;
import org.tron.trident.utils.Numeric;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static io.protostuff.ByteString.EMPTY_STRING;
import static network.nerve.converter.heterogeneouschain.trx.constant.TrxConstant.TRX_1;
import static network.nerve.converter.heterogeneouschain.trx.constant.TrxConstant.TRX_10;
import static org.tron.trident.core.ApiWrapper.parseAddress;
import static org.tron.trident.core.ApiWrapper.parseHex;

/**
 * @author: Mimi
 * @date: 2021/7/27
 */
public class TrxWalletApi implements BeanInitial {

    private HtgContext htgContext;

    private String symbol() {
        return htgContext.getConfig().getSymbol();
    }

    protected ApiWrapper wrapper;

    protected String rpcAddress;
    int switchStatus = 0;
    private boolean inited = false;
    private Map<String, Integer> requestExceededMap = new HashMap<>();
    private long clearTimeOfRequestExceededMap = 0L;

    private Map<String, BigInteger> map = new HashMap<>();
    private ReentrantLock checkLock = new ReentrantLock();
    private ReentrantLock resetLock = new ReentrantLock();
    private ReentrantLock reSignLock = new ReentrantLock();

    private NulsLogger getLog() {
        return htgContext.logger();
    }

    public void init(String rpcAddress) throws NulsException {
        // 默认初始化的API会被新的API服务覆盖，当节点成为虚拟银行时，会初始化新的API服务
        if (StringUtils.isNotBlank(rpcAddress) && htgContext.getConfig().getCommonRpcAddress().equals(this.rpcAddress) && !rpcAddress.equals(this.rpcAddress)) {
            resetApiWrapper();
        }
        if (wrapper == null) {
            wrapper = newInstanceApiWrapper(rpcAddress);
            this.rpcAddress = StringUtils.isBlank(rpcAddress) ? EMPTY_STRING : rpcAddress;
            getLog().info("初始化 {} API URL: {}", symbol(), rpcAddress);
        }
    }

    public void checkApi(int order) throws NulsException {
        long now = System.currentTimeMillis();
        // 如果使用的是应急API，应急API使用时间内，不检查API切换
        if (now < clearTimeOfRequestExceededMap) {
            if (htgContext.getConfig().getMainRpcAddress().equals(this.rpcAddress)) {
                if (getLog().isDebugEnabled()) {
                    getLog().debug("应急API使用时间内，不检查API切换, 到期时间: {}，剩余等待时间: {}", clearTimeOfRequestExceededMap, clearTimeOfRequestExceededMap - now);
                }
                return;
            }
        } else if (clearTimeOfRequestExceededMap != 0){
            getLog().info("重置应急API，{} API 准备切换，当前API: {}", symbol(), this.rpcAddress);
            requestExceededMap.clear();
            clearTimeOfRequestExceededMap = 0L;
        }

        String rpc = this.calRpcBySwitchStatus(order, switchStatus);
        if (!rpc.equals(this.rpcAddress)) {
            checkLock.lock();
            try {
                if (!rpc.equals(this.rpcAddress)) {
                    getLog().info("检测到顺序变化，{} API 准备切换，当前API: {}", symbol(), this.rpcAddress);
                    changeApi(rpc);
                }
            } catch (NulsException e) {
                throw e;
            } finally {
                checkLock.unlock();
            }
        }
    }

    private String calRpcBySwitchStatus(int order, int tempSwitchStatus) throws NulsException {
        String rpc;
        List<String> rpcAddressList = htgContext.RPC_ADDRESS_LIST();
        if(tempSwitchStatus == 1) {
            rpcAddressList = htgContext.STANDBY_RPC_ADDRESS_LIST();
        }
        int rpcSize = rpcAddressList.size();
        if(rpcSize == 1) {
            rpc = rpcAddressList.get(0);
        } else if(rpcSize > 1) {
            int index = this.calRpcIndex(order, rpcSize);
            rpc = rpcAddressList.get(index);
        } else {
            rpc = EMPTY_STRING;
        }
        return rpc;
    }

    private void changeApi(String rpc) throws NulsException {
        resetApiWrapper();
        init(rpc);
    }

    private int calRpcIndex(int order, int rpcSize) {
        if (order == 0) {
            order++;
        }
        int mod = order % rpcSize;
        int index = mod != 0 ? mod : rpcSize;
        index--;
        return index;
    }

    private boolean unavailableRpc(String rpc) {
        Integer count = requestExceededMap.get(rpc);
        return (count != null && count > 5);
    }

    private void switchStandbyAPI(String oldRpc) throws NulsException {
        getLog().info("{} API 准备切换，当前API: {}", symbol(), oldRpc);
        resetLock.lock();
        try {
            // 不相等，说明已经被切换
            if (!oldRpc.equals(this.rpcAddress)) {
                getLog().info("{} API 已切换至: {}", symbol(), this.rpcAddress);
                return;
            }
            int expectSwitchStatus = (switchStatus + 1) % 2;
            int order = htgContext.getConverterCoreApi().getVirtualBankOrder();
            String rpc = this.calRpcBySwitchStatus(order, expectSwitchStatus);
            // 检查配置的API是否超额
            if (unavailableRpc(oldRpc) && unavailableRpc(rpc)) {
                String mainRpcAddress = htgContext.getConfig().getMainRpcAddress();
                mainRpcAddress = StringUtils.isBlank(mainRpcAddress) ? EMPTY_STRING : mainRpcAddress;
                getLog().info("{} API 不可用: {} - {}, 准备切换至应急API: {}, ", symbol(), oldRpc, rpc, mainRpcAddress);
                changeApi(mainRpcAddress);
                if (mainRpcAddress.equals(this.rpcAddress)) {
                    clearTimeOfRequestExceededMap = System.currentTimeMillis() + HtgConstant.HOURS_3;
                }
                return;
            }
            // 正常切换API
            if (!rpc.equals(this.rpcAddress)) {
                changeApi(rpc);
                // 相等，说明切换成功
                if (rpc.equals(this.rpcAddress)) {
                    switchStatus = expectSwitchStatus;
                }
            }
        } catch (NulsException e) {
            throw e;
        } finally {
            resetLock.unlock();
        }
    }

    private void resetApiWrapper() {
        if (wrapper != null) {
            wrapper.close();
            wrapper = null;
        }
    }

    public boolean isInited() {
        return inited;
    }

    public void initedDone() {
        this.inited = true;
    }


    protected void checkIfResetApiWrapper(int times) throws NulsException {
        int mod = times % 6;
        if (mod == 5 && wrapper != null && rpcAddress != null) {
            getLog().info("重启API服务");
            resetApiWrapper();
            wrapper = newInstanceApiWrapper(rpcAddress);
        }
    }

    private ApiWrapper newInstanceApiWrapper(String rpcAddress) throws NulsException {
        if (StringUtils.isBlank(rpcAddress)) {
            return ApiWrapper.ofShasta("3333333333333333333333333333333333333333333333333333333333333333");
        } else {
            return ApiWrapper.ofMainnet("3333333333333333333333333333333333333333333333333333333333333333", rpcAddress);
        }
    }


    public Response.TransactionInfo getTransactionReceipt(String txHash) throws Exception {
        if (StringUtils.isBlank(txHash)) {
            return null;
        }
        txHash = Numeric.cleanHexPrefix(txHash);
        return this.timeOutWrapperFunction("getTxReceipt", txHash, args -> {
            ByteString bsTxid = parseAddress(args);
            GrpcAPI.BytesMessage request = GrpcAPI.BytesMessage.newBuilder()
                    .setValue(bsTxid)
                    .build();
            Response.TransactionInfo transactionInfo = wrapper.blockingStub.getTransactionInfoById(request);

            if(transactionInfo.getBlockTimeStamp() == 0){
                return null;
            }
            return transactionInfo;
        });
    }

    private <T, R> R timeOutWrapperFunction(String functionName, T arg, ExceptionFunction<T, R> fucntion) throws Exception {
        return this.timeOutWrapperFunctionReal(functionName, fucntion, 0, arg);
    }

    private <T, R> R timeOutWrapperFunctionReal(String functionName, ExceptionFunction<T, R> fucntion, int times, T arg) throws Exception {
        try {
            this.checkIfResetApiWrapper(times);
            return fucntion.apply(arg);
        } catch (Exception e) {
            String message = e.getMessage();
            Throwable cause = e.getCause();
            if (cause != null) {
                String causeMessage = cause.getMessage();
                if (causeMessage != null) {
                    message += causeMessage;
                }
            }
            boolean isTimeOut = ConverterUtil.isTimeOutError(message);
            if (isTimeOut) {
                getLog().error("{}: {} function [{}] time out", e.getClass().getName(), symbol(), functionName);
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                return timeOutWrapperFunctionReal(functionName, fucntion, times + 1, arg);
            }
            // 当API连接异常时，重置API连接，使用备用API
            if (e instanceof StatusException || e instanceof StatusRuntimeException) {
                getLog().warn("API连接异常时，重置API连接，使用备用API");
                if (ConverterUtil.isRequestExpired(e.getMessage()) && htgContext.getConfig().getMainRpcAddress().equals(this.rpcAddress)) {
                    getLog().info("重新签名应急API: {}", this.rpcAddress);
                    throw e;
                }
                if (ConverterUtil.isRequestDenied(e.getMessage()) && htgContext.getConfig().getMainRpcAddress().equals(this.rpcAddress)) {
                    getLog().info("重置应急API，{} API 准备切换，当前API: {}", symbol(), this.rpcAddress);
                    requestExceededMap.clear();
                    clearTimeOfRequestExceededMap = 0L;
                    switchStandbyAPI(this.rpcAddress);
                    throw e;
                }
                Integer count = requestExceededMap.computeIfAbsent(this.rpcAddress, r -> 0);
                requestExceededMap.put(this.rpcAddress, count + 1);
                switchStandbyAPI(this.rpcAddress);
                throw e;
            }
            // 若遭遇网络连接异常
            if (e instanceof ConnectException || e instanceof UnknownHostException) {
                // 应急API重置，切换到普通API
                if (htgContext.getConfig().getMainRpcAddress().equals(this.rpcAddress)) {
                    getLog().info("重置应急API，{} API 准备切换，当前API: {}", symbol(), this.rpcAddress);
                    requestExceededMap.clear();
                    clearTimeOfRequestExceededMap = 0L;
                    switchStandbyAPI(this.rpcAddress);
                    throw e;
                }
                // 普通API记录异常次数
                Integer count = requestExceededMap.getOrDefault(this.rpcAddress, 0);
                requestExceededMap.put(this.rpcAddress, count + 1);
                switchStandbyAPI(this.rpcAddress);
                throw e;
            }

            if (e instanceof SSLHandshakeException || e instanceof SSLException) {
                changeApi(this.rpcAddress);
            }
            getLog().error(e.getMessage(), e);
            throw e;
        }
    }


    /**
     * 获取交易详情
     */
    public Chain.Transaction getTransactionByHash(String txHash) throws Exception {
        if (StringUtils.isBlank(txHash)) {
            return null;
        }
        txHash = Numeric.cleanHexPrefix(txHash);
        return this.timeOutWrapperFunction("getTransactionById", txHash, args -> {
            ByteString bsTxid = parseAddress(args);
            GrpcAPI.BytesMessage request = GrpcAPI.BytesMessage.newBuilder().setValue(bsTxid).build();
            Chain.Transaction transaction = wrapper.blockingStub.getTransactionById(request);
            if (transaction.getRetCount() == 0) {
                return null;
            } else {
                return transaction;
            }
        });
    }

    /**
     * 调用合约的view/constant函数
     */
    public List<Type> callViewFunction(String contractAddress, Function function) throws Exception {
        List<Type> typeList = this.timeOutWrapperFunction("callViewFunction", List.of(contractAddress, function), args -> {
            String _contractAddress = (String) args.get(0);
            Function _function = (Function) args.get(1);
            Response.TransactionExtention call = wrapper.constantCall(TrxConstant.ZERO_ADDRESS_TRX, _contractAddress, _function);
            if (call.getConstantResultCount() == 0) {
                return null;
            }
            byte[] bytes = call.getConstantResult(0).toByteArray();
            return FunctionReturnDecoder.decode(Numeric.toHexString(bytes), _function.getOutputParameters());
        });
        return typeList;
    }


    public TrxEstimateSun estimateSunUsed(String from, String contractAddress, Function function) throws Exception {
        return this.estimateSunUsed(from, contractAddress, function, null);
    }

    public TrxEstimateSun estimateSunUsed(String from, String contractAddress, Function function, BigInteger value) throws Exception {
        value = value == null ? BigInteger.ZERO : value;
        TrxEstimateSun estimateEnergy = this.timeOutWrapperFunction("estimateEnergyUsed", List.of(from, contractAddress, function, value), args -> {
            BigInteger _value = (BigInteger) args.get(3);
            String encodedHex = FunctionEncoder.encode(function);
            Contract.TriggerSmartContract trigger = Contract.TriggerSmartContract.newBuilder()
                    .setOwnerAddress(parseAddress(from))
                    .setContractAddress(parseAddress(contractAddress))
                    .setData(parseHex(encodedHex))
                    .setCallValue(_value.longValue())
                    .build();
            Response.TransactionExtention call = wrapper.blockingStub.triggerConstantContract(trigger);

            if (call.getConstantResultCount() == 0) {
                return TrxEstimateSun.FAILED("Execute failed: empty result.");
            }
            String result = Numeric.toHexString(call.getConstantResult(0).toByteArray());
            if (TrxUtil.isErrorInResult(result)) {
                return TrxEstimateSun.FAILED(TrxUtil.getRevertReason(result));
            }
            long sunUsed = 0;
            long energyUsed = call.getEnergyUsed();
            if (energyUsed != 0) {
                sunUsed = BigInteger.valueOf(energyUsed).multiply(TrxConstant.SUN_PER_ENERGY).longValue();
            }
            return TrxEstimateSun.SUCCESS(sunUsed);
        });
        return estimateEnergy;
    }

    public long getBlockHeight() throws Exception {
        long blockHeight = this.timeOutWrapperFunction("getBlockHeight", null, args -> {
            Chain.Block nowBlock = wrapper.getNowBlock();
            return nowBlock.getBlockHeader().getRawDataOrBuilder().getNumber();
        });
        return blockHeight;
    }


    /**
     * 根据高度获取区块
     */
    public Response.BlockExtention getBlockByHeight(Long height) throws Exception {
        Response.BlockExtention block = this.timeOutWrapperFunction("getBlockByHeight", height, args -> {
            long blockNum = args;
            GrpcAPI.NumberMessage.Builder builder = GrpcAPI.NumberMessage.newBuilder();
            builder.setNum(blockNum);
            return wrapper.blockingStub.getBlockByNum2(builder.build());
        });
        if (block == null) {
            getLog().error("获取区块为空");
        }
        return block;
    }

    /**
     * 获取ht余额
     */
    public BigDecimal getBalance(String address) throws Exception {
        BigDecimal balance = this.timeOutWrapperFunction("getAccountBalance", address, args -> {
            long accountBalance = wrapper.getAccountBalance(args);
            return new BigDecimal(accountBalance);
        });
        return balance;
    }

    public TrxSendTransactionPo callContract(String from, String privateKey, String contractAddress, BigInteger feeLimit, Function function) throws Exception {
        return this.callContract(from, privateKey, contractAddress, feeLimit, function, null);
    }

    public TrxSendTransactionPo callContract(String from, String privateKey, String contractAddress, BigInteger feeLimit, Function function, BigInteger value) throws Exception {
        value = value == null ? BigInteger.ZERO : value;
        String encodedFunction = FunctionEncoder.encode(function);

        TrxSendTransactionPo txPo = this.timeOutWrapperFunction("callContract", List.of(from, privateKey, contractAddress, feeLimit, encodedFunction, value), args -> {
            int i =0;
            String _from = args.get(i++).toString();
            String _privateKey = args.get(i++).toString();
            String _contractAddress = args.get(i++).toString();
            BigInteger _feeLimit = (BigInteger) args.get(i++);
            String _encodedFunction = args.get(i++).toString();
            BigInteger _value = (BigInteger) args.get(i++);

            Contract.TriggerSmartContract trigger =
                    Contract.TriggerSmartContract.newBuilder()
                            .setOwnerAddress(parseAddress(_from))
                            .setContractAddress(parseAddress(_contractAddress))
                            .setData(parseHex(_encodedFunction))
                            .setCallValue(_value.longValue())
                            .build();
            Response.TransactionExtention txnExt = wrapper.blockingStub.triggerContract(trigger);
            TransactionBuilder builder = new TransactionBuilder(txnExt.getTransaction());
            builder.setFeeLimit(_feeLimit.longValue());

            Chain.Transaction signedTxn = wrapper.signTransaction(builder.build(), new KeyPair(_privateKey));
            Response.TransactionReturn ret = wrapper.blockingStub.broadcastTransaction(signedTxn);
            if (!ret.getResult()) {
                getLog().error("[{}]调用合约交易广播失败, 原因: {}", symbol(), ret.getMessage().toStringUtf8());
                return null;
            }
            return new TrxSendTransactionPo(TrxUtil.calcTxHash(signedTxn), _from, _contractAddress, _value, _encodedFunction, _feeLimit);
        });
        return txPo;
    }


    public void setRpcAddress(String rpcAddress) {
        this.rpcAddress = rpcAddress;
    }

    public ApiWrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(ApiWrapper wrapper) {
        this.wrapper = wrapper;
    }

    /**
     * 获取ERC-20 token指定地址余额
     *
     * @param address         查询地址
     * @param contractAddress 合约地址
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public BigInteger getERC20Balance(String address, String contractAddress) throws Exception {
        return this.getERC20BalanceReal(address, contractAddress, 0);
    }

    private BigInteger getERC20BalanceReal(String address, String contractAddress, int times) throws Exception {
        try {
            this.checkIfResetApiWrapper(times);
            Function function = new Function("balanceOf",
                    Arrays.asList(new Address(address)),
                    Arrays.asList(new TypeReference<Uint256>() {
                    }));
            List<Type> types = callViewFunction(contractAddress, function);
            if (types == null || types.isEmpty()) {
                return BigInteger.ZERO;
            }
            return (BigInteger) types.get(0).getValue();
        } catch (Exception e) {
            String message = e.getMessage();
            boolean isTimeOut = ConverterUtil.isTimeOutError(message);
            if (isTimeOut) {
                getLog().error("{} ERC20 balance time out", symbol());
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                return getERC20BalanceReal(address, contractAddress, times + 1);
            } else {
                getLog().error(e.getMessage(), e);
                throw e;
            }
        }

    }

    public TrxSendTransactionPo transferTRC20Token(String from, String to, BigInteger value, String privateKey, String contractAddress) throws Exception {
        return this.transferTRC20Token(from, to, value, privateKey, contractAddress, null);
    }

    public TrxSendTransactionPo transferTRC20Token(String from, String to, BigInteger value, String privateKey, String contractAddress, BigInteger feeLimit) throws Exception {
        feeLimit = feeLimit == null ? TRX_10 : feeLimit;
        //创建RawTransaction交易对象
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(to), new Uint256(value)),
                Arrays.asList(new TypeReference<Type>() {}));
        TrxSendTransactionPo callContract = this.callContract(from, privateKey, contractAddress, feeLimit, function);
        return callContract;
    }

    public TrxSendTransactionPo transferTrx(String from, String to, BigInteger value, String privateKey) throws Exception {
        TrxSendTransactionPo transferTrx = this.timeOutWrapperFunction("transferTrx", List.of(from, to, value, privateKey), args -> {
            Response.TransactionExtention txnExt = wrapper.transfer(from, to, value.longValue());

            TransactionBuilder builder = new TransactionBuilder(txnExt.getTransaction());
            builder.setFeeLimit(TRX_1.longValue());

            Chain.Transaction signedTxn = wrapper.signTransaction(builder.build(), new KeyPair(privateKey));
            Response.TransactionReturn ret = wrapper.blockingStub.broadcastTransaction(signedTxn);
            if (!ret.getResult()) {
                getLog().error("[{}]转账交易广播失败, 原因: {}", symbol(), ret.getMessage().toStringUtf8());
                return null;
            }
            return new TrxSendTransactionPo(TrxUtil.calcTxHash(signedTxn), from, to, value, null, TRX_1);
        });
        return transferTrx;
    }
}
