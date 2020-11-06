package network.nerve.converter.heterogeneouschain.ethII.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nuls.core.parse.JSONUtils;
import network.nerve.converter.enums.HeterogeneousChainTxType;
import network.nerve.converter.heterogeneouschain.eth.constant.EthConstant;
import network.nerve.converter.heterogeneouschain.eth.context.EthContext;
import network.nerve.converter.heterogeneouschain.eth.helper.EthERC20Helper;
import network.nerve.converter.heterogeneouschain.eth.model.EthUnconfirmedTxPo;
import network.nerve.converter.heterogeneouschain.eth.utils.EthUtil;
import network.nerve.converter.heterogeneouschain.ethII.base.BaseII;
import network.nerve.converter.heterogeneouschain.ethII.constant.EthIIConstant;
import network.nerve.converter.heterogeneouschain.ethII.helper.EthIIParseTxHelper;
import network.nerve.converter.heterogeneouschain.ethII.utils.EthIIUtil;
import network.nerve.converter.model.bo.HeterogeneousTransactionBaseInfo;
import org.junit.Before;
import org.junit.Test;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.*;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class ETHIIWalletApiTest extends BaseII {

    protected String from;
    protected String fromPriKey;
    protected String erc20Address;
    protected int erc20Decimals;

    protected void setErc20USDI() {
        erc20Address = "0x1c78958403625aeA4b0D5a0B527A27969703a270";
        erc20Decimals = 6;
    }
    protected void setErc20USDX() {
        erc20Address = "0xB058887cb5990509a3D0DD2833B2054E4a7E4a55";
        erc20Decimals = 6;
    }
    protected void setErc20NVT() {
        erc20Address = "0x25EbbAC2CA9DB0c1d6F0fc959BbC74985417BaB0";
        erc20Decimals = 8;
    }
    protected void setErc20NULS() {
        erc20Address = "0x79D7c11CC945a1734d21Ef41e631EFaE894Af2C3";
        erc20Decimals = 8;
    }
    protected void setAccount_c684() {
        from = "0xfa27c84eC062b2fF89EB297C24aaEd366079c684";
        fromPriKey = "b36097415f57fe0ac1665858e3d007ba066a7c022ec712928d2372b27e8513ff";
    }
    protected void setAccount_EFa1() {
        from = "0xc11D9943805e56b630A401D4bd9A29550353EFa1";
        fromPriKey = "4594348E3482B751AA235B8E580EFEF69DB465B3A291C5662CEDA6459ED12E39";
    }

    @Before
    public void before() {

    }

    protected void setDev() {
        list = new ArrayList<>();
        list.add("9ce21dad67e0f0af2599b41b515a7f7018059418bab892a7b68f283d489abc4b");// 0xbe7fbb51979e0b0b70c48284e895e228290d9f73
        list.add("477059f40708313626cccd26f276646e4466032cabceccbf571a7c46f954eb75");// 0xcb1fa0c0b7b4d57848bddaa4276ce0776a3215d2
        list.add("8212e7ba23c8b52790c45b0514490356cd819db15d364cbe08659b5888339e78");// 0x54103606d9fcdb40539d06344c8f8c6367ffc9b8
        list.add("4100e2f88c3dba08e5000ed3e8da1ae4f1e0041b856c09d35a26fb399550f530");// 0x847772e7773061c93d0967c56f2e4b83145c8cb2
        list.add("bec819ef7d5beeb1593790254583e077e00f481982bce1a43ea2830a2dc4fdf7");// 0x4273e90fbfe5fca32704c02987d938714947a937

        list.add("ddddb7cb859a467fbe05d5034735de9e62ad06db6557b64d7c139b6db856b200");// 0x9f14432b86db285c76589d995aab7e7f88b709df
        list.add("4efb6c23991f56626bc77cdb341d64e891e0412b03cbcb948aba6d4defb4e60a");// 0x42868061f6659e84414e0c52fb7c32c084ce2051
        list.add("3dadac00b523736f38f8c57deb81aa7ec612b68448995856038bd26addd80ec1");// 0x26ac58d3253cbe767ad8c14f0572d7844b7ef5af
        list.add("27dbdcd1f2d6166001e5a722afbbb86a845ef590433ab4fcd13b9a433af6e66e");// 0x9dc0ec60c89be3e5530ddbd9cf73430e21237565
        list.add("76b7beaa98db863fb680def099af872978209ed9422b7acab8ab57ad95ab218b");// 0x6392c7ed994f7458d60528ed49c2f525dab69c9a

        list.add("B36097415F57FE0AC1665858E3D007BA066A7C022EC712928D2372B27E8513FF");// 0xfa27c84ec062b2ff89eb297c24aaed366079c684
        list.add("4594348E3482B751AA235B8E580EFEF69DB465B3A291C5662CEDA6459ED12E39");// 0xc11d9943805e56b630a401d4bd9a29550353efa1
        list.add("e70ea2ebe146d900bf84bc7a96a02f4802546869da44a23c29f599c7e42001da");// 0x3091e329908da52496cc72f5d5bbfba985bccb1f
        list.add("4c6b4c5d9b07e364d6b306d1afe0f2c37e15c64ac5151a395a4c570f00ce867d");// 0x49467643f1b6459caf316866ecef9213edc4fdf2
        list.add("2fea28f438a104062e4dcd79427282573053a6b762e68b942055221462c46f02");// 0x5e57d62ab168cd69e0808a73813fbf64622b3dfd

        list.add("08407198c196c950afffd326a00321a5ea563b3beaf640d462f3a274319b753d");// 0x7dc432b48d813b2579a118e5a0d2fee744ac8e02 16
        this.multySignContractAddress = "";
        init();
    }
    protected void setLocalTest() {
        list = new ArrayList<>();
        list.add("b54db432bba7e13a6c4a28f65b925b18e63bcb79143f7b894fa735d5d3d09db5");// 0xdd7CBEdDe731e78e8b8E4b2c212bC42fA7C09D03
        list.add("188b255c5a6d58d1eed6f57272a22420447c3d922d5765ebb547bc6624787d9f");// 0xD16634629C638EFd8eD90bB096C216e7aEc01A91
        list.add("fbcae491407b54aa3904ff295f2d644080901fda0d417b2b427f5c1487b2b499");// 0x16534991E80117Ca16c724C991aad9EAbd1D7ebe
        list.add("43DA7C269917207A3CBB564B692CD57E9C72F9FCFDB17EF2190DD15546C4ED9D");// 0x8F05AE1C759b8dB56ff8124A89bb1305ECe17B65
        list.add("0935E3D8C87C2EA5C90E3E3A0509D06EB8496655DB63745FAE4FF01EB2467E85");// 0xd29E172537A3FB133f790EBE57aCe8221CB8024F
        list.add("CCF560337BA3DE2A76C1D08825212073B299B115474B65DE4B38B587605FF7F2");// 0x54eAB3868B0090E6e1a1396E0e54F788a71B2b17
        this.multySignContractAddress = "0xdcb777E7491f03D69cD10c1FeE335C9D560eb5A2";
        init();
    }
    protected void setBeta() {
        list.add("978c643313a0a5473bf65da5708766dafc1cca22613a2480d0197dc99183bb09");// 0x1a9f8b818a73b0f9fde200cd88c42b626d2661cd
        list.add("6e905a55d622d43c499fa844c05db46859aed9bb525794e2451590367e202492");// 0x6c2039b5fdae068bad4931e8cc0b8e3a542937ac
        list.add("d48b870f2cf83a739a134cd19015ed96d377f9bc9e6a41108ac82daaca5465cf");// 0x3c2ff003ff996836d39601ca22394a58ca9c473b
        this.multySignContractAddress = "0x7D759A3330ceC9B766Aa4c889715535eeD3c0484";
        init();
    }

    public void init() {
        EthContext.setEthGasPrice(BigInteger.valueOf(10L).multiply(BigInteger.TEN.pow(9)));
        this.address = Credentials.create(list.get(0)).getAddress();
        this.priKey = list.get(0);
    }

    @Test
    public void eventHashTest() {
        System.out.println(String.format("event: %s, hash: %s", EthIIConstant.EVENT_TRANSACTION_WITHDRAW_COMPLETED.getName(), EventEncoder.encode(EthIIConstant.EVENT_TRANSACTION_WITHDRAW_COMPLETED)));
        System.out.println(String.format("event: %s, hash: %s", EthIIConstant.EVENT_TRANSACTION_MANAGER_CHANGE_COMPLETED.getName(), EventEncoder.encode(EthIIConstant.EVENT_TRANSACTION_MANAGER_CHANGE_COMPLETED)));
        System.out.println(String.format("event: %s, hash: %s", EthIIConstant.EVENT_TRANSACTION_UPGRADE_COMPLETED.getName(), EventEncoder.encode(EthIIConstant.EVENT_TRANSACTION_UPGRADE_COMPLETED)));
    }

    @Test
    public void methodHashTest() {
        Function upgradeFunction = EthIIUtil.getCreateOrSignUpgradeFunction("", "0x5e57d62ab168cd69e0808a73813fbf64622b3dfd", "0x");
        System.out.println(String.format("event: %s, hash: %s", upgradeFunction.getName(), FunctionEncoder.encode(upgradeFunction)));
        Function withdrawFunction = EthIIUtil.getCreateOrSignWithdrawFunction("", "0x5e57d62ab168cd69e0808a73813fbf64622b3dfd", BigInteger.ZERO, false, "0x5e57d62ab168cd69e0808a73813fbf64622b3dfd", "0x");
        System.out.println(String.format("event: %s, hash: %s", withdrawFunction.getName(), FunctionEncoder.encode(withdrawFunction)));
        Function changeFunction = EthIIUtil.getCreateOrSignManagerChangeFunction("", List.of(), List.of(), 1, "0x");
        System.out.println(String.format("event: %s, hash: %s", changeFunction.getName(), FunctionEncoder.encode(changeFunction)));
    }

    /**
     * 给多签合约转入eth和erc20（用于测试提现）
     */
    @Test
    public void transferETHAndERC20() throws Exception {
        BigInteger gasPrice = EthContext.getEthGasPrice();
        // 初始化 账户
        setAccount_EFa1();
        // ETH数量
        String sendAmount = "0.1";
        String txHash = ethWalletApi.sendETH(from, fromPriKey, multySignContractAddress, new BigDecimal(sendAmount), BigInteger.valueOf(81000L), gasPrice);
        System.out.println(String.format("向[%s]转账%s个ETH, 交易hash: %s", multySignContractAddress, sendAmount, txHash));
        // ERC20
        String tokenAddress = "0x1c78958403625aeA4b0D5a0B527A27969703a270";
        String tokenAmount = "100";
        int tokenDecimals = 6;
        EthSendTransaction token = ethWalletApi.transferERC20Token(from, multySignContractAddress, new BigInteger(tokenAmount).multiply(BigInteger.TEN.pow(tokenDecimals)), fromPriKey, tokenAddress);
        System.out.println(String.format("向[%s]转账%s个ERC20(USDI), 交易hash: %s", multySignContractAddress, tokenAmount, token.getTransactionHash()));
    }

    /**
     * 新方式充值eth
     */
    @Test
    public void depositETHByCrossOut() throws Exception {
        setLocalTest();
        // 初始化 账户
        setAccount_EFa1();
        // ETH数量
        String sendAmount = "0.1";
        // Nerve 接收地址
        String to = "TNVTdTSPRnXkDiagy7enti1KL75NU5AxC9sQA";
        BigInteger convertAmount = ethWalletApi.convertEthToWei(new BigDecimal(sendAmount));
        Function crossOutFunction = EthIIUtil.getCrossOutFunction(to, convertAmount, EthConstant.ZERO_ADDRESS);
        String hash = this.sendTx(from, fromPriKey, crossOutFunction, HeterogeneousChainTxType.DEPOSIT, convertAmount, multySignContractAddress);
        System.out.println(String.format("eth充值[%s], hash: %s", sendAmount, hash));
    }
    /**
     * 新方式充值erc20
     */
    @Test
    public void depositERC20ByCrossOut() throws Exception {
        setLocalTest();
        EthContext.setEthGasPrice(BigInteger.valueOf(10L).multiply(BigInteger.TEN.pow(9)));
        // 初始化 账户
        setAccount_EFa1();
        // ERC20 转账数量
        String sendAmount = "3";
        // 初始化 ERC20 地址信息
        //setErc20USDI();
        //setErc20USDX();
        setErc20NVT();
        //setErc20NULS();
        // Nerve 接收地址
        String to = "TNVTdTSPRnXkDiagy7enti1KL75NU5AxC9sQA";

        BigInteger convertAmount = new BigInteger(sendAmount).multiply(BigInteger.TEN.pow(erc20Decimals));
        Function allowanceFunction = new Function("allowance",
                Arrays.asList(new Address(from), new Address(multySignContractAddress)),
                Arrays.asList(new TypeReference<Uint256>() {}));

        BigInteger allowanceAmount = (BigInteger) ethWalletApi.callViewFunction(erc20Address, allowanceFunction).get(0).getValue();
        if (allowanceAmount.compareTo(convertAmount) < 0) {
            // erc20授权
            Function approveFunction = this.getERC20ApproveFunction(multySignContractAddress, convertAmount);
            String authHash = this.sendTx(from, fromPriKey, approveFunction, HeterogeneousChainTxType.DEPOSIT, null, erc20Address);
            System.out.println(String.format("erc20授权充值[%s], 授权hash: %s", sendAmount, authHash));
            while (ethWalletApi.getTxReceipt(authHash) == null) {
                System.out.println("等待8秒查询[ERC20授权]交易打包结果");
                TimeUnit.SECONDS.sleep(8);
            }
            TimeUnit.SECONDS.sleep(8);
            BigInteger tempAllowanceAmount = (BigInteger) ethWalletApi.callViewFunction(erc20Address, allowanceFunction).get(0).getValue();
            while (tempAllowanceAmount.compareTo(convertAmount) < 0) {
                System.out.println("等待8秒查询[ERC20授权]交易额度");
                TimeUnit.SECONDS.sleep(8);
                tempAllowanceAmount = (BigInteger) ethWalletApi.callViewFunction(erc20Address, allowanceFunction).get(0).getValue();
            }
        }
        System.out.println("[ERC20授权]额度已满足条件");
        // crossOut erc20转账
        Function crossOutFunction = EthIIUtil.getCrossOutFunction(to, convertAmount, erc20Address);
        String hash = this.sendTx(from, fromPriKey, crossOutFunction, HeterogeneousChainTxType.DEPOSIT);
        System.out.println(String.format("erc20充值[%s], 充值hash: %s", sendAmount, hash));
    }

    @Test
    public void registerERC20Minter() throws Exception {
        // 正式网数据
        setMain();
        // GasPrice准备
        long gasPriceGwei = 100L;
        BigInteger gasPrice = BigInteger.valueOf(gasPriceGwei).multiply(BigInteger.TEN.pow(9));
        // 超级账户，加载凭证，用私钥
        Credentials credentials = Credentials.create("");
        // 多签合约地址
        String contractAddress = "0x6758d4C4734Ac7811358395A8E0c3832BA6Ac624";
        // 注册的ERC20Minter合约地址
        String erc20Minter = "0x7b6F71c8B123b38aa8099e0098bEC7fbc35B8a13";

        EthGetTransactionCount transactionCount = ethWalletApi.getWeb3j().ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.PENDING).sendAsync().get();
        BigInteger nonce = transactionCount.getTransactionCount();
        //创建RawTransaction交易对象
        Function function = new Function(
                "registerMinterERC20",
                List.of(new Address(erc20Minter)),
                List.of(new TypeReference<Type>() {
                })
        );

        String encodedFunction = FunctionEncoder.encode(function);

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                BigInteger.valueOf(50000L),
                contractAddress, encodedFunction
        );
        //签名Transaction，这里要对交易做签名
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signMessage);
        //发送交易
        EthSendTransaction ethSendTransaction = ethWalletApi.getWeb3j().ethSendRawTransaction(hexValue).sendAsync().get();
        System.out.println(ethSendTransaction.getTransactionHash());
    }

    /**
     * 还原合约管理员
     */
    @Test
    public void resetContractManager() throws Exception {
        setLocalTest();
        // 0x8F05AE1C759b8dB56ff8124A89bb1305ECe17B65,0xd29E172537A3FB133f790EBE57aCe8221CB8024F,0x54eAB3868B0090E6e1a1396E0e54F788a71B2b17
        String txKey = "bbb8000000000000000000000000000000000000000000000000000000000000";
        String[] adds = new String[]{};
        /*String[] removes = new String[]{
                "0x54eAB3868B0090E6e1a1396E0e54F788a71B2b17",
                "0x8F05AE1C759b8dB56ff8124A89bb1305ECe17B65",
                "0xd29E172537A3FB133f790EBE57aCe8221CB8024F"
        };*/
        String[] removes = new String[]{
                "0x8F05AE1C759b8dB56ff8124A89bb1305ECe17B65"
        };
        int txCount = 1;
        int signCount = 6;
        String hash = this.sendChange(txKey, adds, txCount, removes, signCount);
        System.out.println(String.format("管理员添加%s个，移除%s个，%s个签名，hash: %s", adds.length, removes.length, signCount, hash));
    }

    protected void setUpgradeMain() {
        setMain();
        //TODO ncf配置文件里，前三个种子节点的出块地址的私钥
        list.add("");// 0xd87f2ad3ef011817319fd25454fc186ca71b3b56
        list.add("");// 0x0eb9e4427a0af1fa457230bef3481d028488363e
        list.add("");// 0xd6946039519bccc0b302f89493bec60f4f0b4610
        this.multySignContractAddress = "0x6758d4C4734Ac7811358395A8E0c3832BA6Ac624";
        init();
    }

    /**
     * 添加 N 个管理员
     */
    @Test
    public void managerAdd() throws Exception {
        // 正式网环境数据
        setUpgradeMain();
        // GasPrice准备
        long gasPriceGwei = 100L;
        EthContext.setEthGasPrice(BigInteger.valueOf(gasPriceGwei).multiply(BigInteger.TEN.pow(9)));
        String txKey = "aaa0000000000000000000000000000000000000000000000000000000000000";
        String[] adds = new String[]{"0x4caa0869a4e0a4a860143b366f336fcc5d11d4d8", "0xb12a6716624431730c3ef55f80c458371954fa52", "0x659ec06a7aedf09b3602e48d0c23cd3ed8623a88", "0x196c4b2b6e947b57b366967a1822f3fb7d9be1a8", "0x1f13e90daa9548defae45cd80c135c183558db1f", "0x66fb6d6df71bbbf1c247769ba955390710da40a5", "0xbb5ba69105a330218e4a433f5e2a273bf0075e64", "0x16525740c7bc9ca4b83532dfb894bd4f42c5ade1", "0x6c9783cc9c9ff9c0f1280e4608afaadf08cfb43d", "0xa28035bb5082f5c00fa4d3efc4cb2e0645167444"};
        String[] removes = new String[]{};
        int txCount = 1;
        int signCount = 3;
        String hash = this.sendChange(txKey, adds, txCount, removes, signCount);
        System.out.println(String.format("管理员添加%s个，移除%s个，%s个签名，hash: %s", adds.length, removes.length, signCount, hash));
    }

    /**
     * 添加10个管理员，4个签名
     */
    @Test
    public void managerAdd10By4Managers() throws Exception {
        String txKey = "aaa0000000000000000000000000000000000000000000000000000000000000";
        String[] adds = new String[]{"0x9f14432b86db285c76589d995aab7e7f88b709df", "0x42868061f6659e84414e0c52fb7c32c084ce2051", "0x26ac58d3253cbe767ad8c14f0572d7844b7ef5af", "0x9dc0ec60c89be3e5530ddbd9cf73430e21237565", "0x6392c7ed994f7458d60528ed49c2f525dab69c9a", "0xfa27c84ec062b2ff89eb297c24aaed366079c684", "0xc11d9943805e56b630a401d4bd9a29550353efa1", "0x3091e329908da52496cc72f5d5bbfba985bccb1f", "0x49467643f1b6459caf316866ecef9213edc4fdf2", "0x5e57d62ab168cd69e0808a73813fbf64622b3dfd"};
        String[] removes = new String[]{};
        int txCount = 1;
        int signCount = 4;
        String hash = this.sendChange(txKey, adds, txCount, removes, signCount);
        System.out.println(String.format("管理员添加%s个，移除%s个，%s个签名，hash: %s", adds.length, removes.length, signCount, hash));
    }

    /**
     * 顶替一个管理员，10个签名
     */
    @Test
    public void managerReplace1By10Managers() throws Exception {
        String txKey = "bbb0000000000000000000000000000000000000000000000000000000000000";
        String[] adds = new String[]{"0x7dc432b48d813b2579a118e5a0d2fee744ac8e02"};
        String[] removes = new String[]{"0x5e57d62ab168cd69e0808a73813fbf64622b3dfd"};
        int txCount = 1;
        int signCount = 10;
        String hash = this.sendChange(txKey, adds, txCount, removes, signCount);
        System.out.println(String.format("管理员添加%s个，移除%s个，%s个签名，hash: %s", adds.length, removes.length, signCount, hash));
    }

    /**
     * 顶替一个管理员，15个签名
     */
    @Test
    public void managerReplace1By15Managers() throws Exception {
        String txKey = "ccc0000000000000000000000000000000000000000000000000000000000000";
        String[] adds = new String[]{"0x5e57d62ab168cd69e0808a73813fbf64622b3dfd"};
        String[] removes = new String[]{"0x7dc432b48d813b2579a118e5a0d2fee744ac8e02"};
        int txCount = 1;
        int signCount = 15;
        String hash = this.sendChange(txKey, adds, txCount, removes, signCount);
        System.out.println(String.format("管理员添加%s个，移除%s个，%s个签名，hash: %s", adds.length, removes.length, signCount, hash));
    }

    /**
     * eth提现，15个签名
     */
    @Test
    public void ethWithdrawBy15Managers() throws Exception {
        String txKey = "ddd0000000000000000000000000000000000000000000000000000000000000";
        String toAddress = "0xc11d9943805e56b630a401d4bd9a29550353efa1";
        String value = "0.01";
        int signCount = 15;
        String hash = this.sendETHWithdraw(txKey, toAddress, value, signCount);
        System.out.println(String.format("ETH提现%s个，%s个签名，hash: %s", value, signCount, hash));
    }

    /**
     * eth提现，10个签名
     */
    @Test
    public void ethWithdrawBy10Managers() throws Exception {
        String txKey = "eee0000000000000000000000000000000000000000000000000000000000000";
        String toAddress = "0xc11d9943805e56b630a401d4bd9a29550353efa1";
        String value = "0.02";
        int signCount = 10;
        String hash = this.sendETHWithdraw(txKey, toAddress, value, signCount);
        System.out.println(String.format("ETH提现%s个，%s个签名，hash: %s", value, signCount, hash));
    }

    /**
     * erc20提现，10个签名
     */
    @Test
    public void erc20WithdrawBy10Managers() throws Exception {
        String txKey = "fff0000000000000000000000000000000000000000000000000000000000000";
        String toAddress = "0xc11d9943805e56b630a401d4bd9a29550353efa1";
        String value = "20";
        String erc20 = "0x1c78958403625aeA4b0D5a0B527A27969703a270";
        int tokenDecimals = 6;
        int signCount = 10;
        String hash = this.sendERC20Withdraw(txKey, toAddress, value, erc20, tokenDecimals, signCount);
        System.out.println(String.format("ERC20提现%s个，%s个签名，hash: %s", value, signCount, hash));
    }

    /**
     * erc20提现，15个签名
     */
    @Test
    public void erc20WithdrawBy15Managers() throws Exception {
        String txKey = "ggg0000000000000000000000000000000000000000000000000000000000000";
        String toAddress = "0xc11d9943805e56b630a401d4bd9a29550353efa1";
        String value = "30";
        String erc20 = "0x1c78958403625aeA4b0D5a0B527A27969703a270";
        int tokenDecimals = 6;
        int signCount = 15;
        String hash = this.sendERC20Withdraw(txKey, toAddress, value, erc20, tokenDecimals, signCount);
        System.out.println(String.format("ERC20提现%s个，%s个签名，hash: %s", value, signCount, hash));
    }

    /**
     * erc20提现，增发的ERC20
     */
    @Test
    public void erc20WithdrawWithERC20Minter() throws Exception {
        String txKey = "ggg0000000000000000000000000000000000000000000000000000000000000";
        String toAddress = "0xc11d9943805e56b630a401d4bd9a29550353efa1";
        String value = "30";
        String erc20 = "0x9c15Fc332D1AefA1c8E9EFbF11FB61a8867975F9";
        int tokenDecimals = 2;
        int signCount = 4;
        String hash = this.sendERC20Withdraw(txKey, toAddress, value, erc20, tokenDecimals, signCount);
        System.out.println(String.format("ERC20提现%s个，%s个签名，hash: %s", value, signCount, hash));
    }

    /**
     * eth提现，异常测试
     */
    @Test
    public void errorETHWithdrawTest() throws Exception {
        String txKey = "h500000000000000000000000000000000000000000000000000000000000000";
        String toAddress = "0xc11d9943805e56b630a401d4bd9a29550353efa1";
        String value = "0.01";
        int signCount = 11;
        list.addAll(5, list.subList(5, 10));
        //this.VERSION = 2;
        String hash = this.sendETHWithdraw(txKey, toAddress, value, signCount);
        System.out.println(String.format("ETH提现%s个，%s个签名，hash: %s", value, signCount, hash));
    }

    /**
     * erc20提现，异常测试
     */
    @Test
    public void errorErc20WithdrawTest() throws Exception {
        String txKey = "I000000000000000000000000000000000000000000000000000000000000000";
        String toAddress = "0xc11d9943805e56b630a401d4bd9a29550353efa1";
        String value = "30";
        String erc20 = "0x1c78958403625aeA4b0D5a0B527A27969703a270";
        int tokenDecimals = 6;
        int signCount = 15;
        String hash = this.sendERC20Withdraw(txKey, toAddress, value, erc20, tokenDecimals, signCount);
        System.out.println(String.format("ERC20提现%s个，%s个签名，hash: %s", value, signCount, hash));
    }

    /**
     * 管理员变更，异常测试
     */
    @Test
    public void errorChangeTest() throws Exception {
        String txKey = "J000000000000000000000000000000000000000000000000000000000000000";
        String[] adds = new String[]{"0x5e57d62ab168cd69e0808a73813fbf64622b3dfd"};
        String[] removes = new String[]{"0x7dc432b48d813b2579a118e5a0d2fee744ac8e02"};
        int txCount = 1;
        int signCount = 15;
        String hash = this.sendChange(txKey, adds, txCount, removes, signCount);
        System.out.println(String.format("管理员添加%s个，移除%s个，%s个签名，hash: %s", adds.length, removes.length, signCount, hash));
    }

    @Test
    public void txInputCrossOutDecoderTest() throws JsonProcessingException {
        String input = "0x0889d1f00000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000bebc20000000000000000000000000025ebbac2ca9db0c1d6f0fc959bbc74985417bab00000000000000000000000000000000000000000000000000000000000000025544e565464545350526e586b446961677937656e7469314b4c37354e553541784339735141000000000000000000000000000000000000000000000000000000";
        List<Object> typeList = EthUtil.parseInput(input, EthIIConstant.INPUT_CROSS_OUT);
        System.out.println(JSONUtils.obj2PrettyJson(typeList));

    }

    @Test
    public void txInputWithdrawDecoderTest() throws JsonProcessingException {
        String input = "0xab6c2b1000000000000000000000000000000000000000000000000000000000000000c0000000000000000000000000c11d9943805e56b630a401d4bd9a29550353efa1000000000000000000000000000000000000000000000000002386f26fc1000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000012000000000000000000000000000000000000000000000000000000000000000403332643634393162316137326661653666346265333030373339393764313463656433346162383761663464633332656634663033663631396464653730316300000000000000000000000000000000000000000000000000000000000001044b65e916f0e20d818f41b369b6f5791e03091c66843c02e305b4d7fcdbfa9e930502002577a22ba6da0ff139d20cd99d430b55d9780dc150babcc9663463cbcd1c1ed9df575fce94ed61a0c487a1065143459dcbddc323805fbeb6230dccf363db059e45517aa39e361eeb44e443d53f3ef5766e92f4bb5eb413a96e8fefd57bcd1cc4574e02eba9ca4768ee84e3302d8dc683fac1cc8db548c28ac831f0e93f59a1631f8d21908346a5eb8ca53556199c750900dfe65fd2520e77fd75cda6b5124c1bd37c1ae577af0099f310a70a3ba2f433683236f4cbf0c88dc0c53eac20e15ab074fcda373704d5ef6e94c417a4e8438eb361e3471a81cf03551e62acf601bd2e1b00000000000000000000000000000000000000000000000000000000";
        List<Object> typeList = EthUtil.parseInput(input, EthIIConstant.INPUT_WITHDRAW);
        System.out.println(JSONUtils.obj2PrettyJson(typeList));

    }

    @Test
    public void txInputChangeDecoderTest() throws JsonProcessingException {
        String changeInput = "0x0071922600000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000012000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000160000000000000000000000000000000000000000000000000000000000000004062626237303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030303030000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000000000008f05ae1c759b8db56ff8124a89bb1305ece17b65000000000000000000000000000000000000000000000000000000000000018622ea0e57097e0ade0f29ec30c3175e26968c44fa48a42bf4a8aae5107ef7e06b2c2d2eb4ed205888278404bd670e59d5071cbf9482e58c3b874362bc6ae923441c31b542c42c81b5f22a584893e6b0de5a5d3f56659c8e4c63241f48b6452f21082ba7f16b1c1050e92aabd95f2ba41bc329c6d23bd46a45e45e256b06e0e6964b1cf602f385625035fa67eef6bb3f210bf426377d4db11d3f4a1bd509dda49665f31e6fa6b9aaf385c5d5548d8dbfb884fc5f2b0262c343a0bfd40612b2009242311c8d24c78cc3a03e2af980a90ba27400ffe64dd52bae055dd1a4f27c76b898e5fe6343c6a8704120f350635387b8b04e84277c06c7a9131dc4077ec9482b99cca31b4285d50efa9568c60336144f063ab3d2d2c8a0cea6f3f30ab3b39bdc2994add63ffaae9e1fb07fb6d2f0e622a8eed50c9ed28673122b00a5e7a65e595bd938821c40cb87676e728e7563be0c1381ccb7a25dc9030dd9edc7235c192dde8a65637534a4f516def65a9036af04759dcf30e408788ad00724646e4939a915d233b9171b0000000000000000000000000000000000000000000000000000";
        List<Object> typeListOfChange = EthUtil.parseInput(changeInput, EthIIConstant.INPUT_CHANGE);
        System.out.println(JSONUtils.obj2PrettyJson(typeListOfChange));
    }

    @Test
    public void encoderWithdrawTest() {
        String txKey = "ddd0000000000000000000000000000000000000000000000000000000000000";
        String toAddress = "0xc11d9943805e56b630a401d4bd9a29550353efa1";
        BigInteger value = BigInteger.valueOf(10000000000000000L);
        Boolean isContractAsset = false;
        String erc20 = "0x0000000000000000000000000000000000000000";
        String hash = this.encoderWithdraw(txKey, toAddress, value, isContractAsset, erc20, (byte) 2);
        System.out.println(String.format("hash: %s", hash));
    }

    @Test
    public void encoderChangeTest() {
        String txKey = "aaa0000000000000000000000000000000000000000000000000000000000000";
        String[] adds = new String[]{"0x9f14432b86db285c76589d995aab7e7f88b709df", "0x42868061f6659e84414e0c52fb7c32c084ce2051", "0x26ac58d3253cbe767ad8c14f0572d7844b7ef5af", "0x9dc0ec60c89be3e5530ddbd9cf73430e21237565", "0x6392c7ed994f7458d60528ed49c2f525dab69c9a", "0xfa27c84ec062b2ff89eb297c24aaed366079c684", "0xc11d9943805e56b630a401d4bd9a29550353efa1", "0x3091e329908da52496cc72f5d5bbfba985bccb1f", "0x49467643f1b6459caf316866ecef9213edc4fdf2", "0x5e57d62ab168cd69e0808a73813fbf64622b3dfd"};
        int count = 1;
        String[] removes = new String[]{};
        String hash = this.encoderChange(txKey, adds, count, removes, (byte) 2);
        System.out.println(String.format("hash: %s", hash));
    }

    @Test
    public void changeEventTest() throws Exception {
        String data = "0x000000000000000000000000742e9290053f63f38270b64b1a8daf52c91e6a510000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000000000000000000000000000271000000000000000000000000080b47d949b4bbd09bb48300c81e2c30df243310c00000000000000000000000000000000000000000000000000000000000000264e4552564565706236426b3765474b776e33546e6e373534633244674b65784b4c5467325a720000000000000000000000000000000000000000000000000000";
        List<Object> eventResult = EthUtil.parseEvent(data, EthIIConstant.EVENT_CROSS_OUT_FUNDS);
        System.out.println(JSONUtils.obj2PrettyJson(eventResult));
    }

    @Test
    public void ethSign() {
        String hashStr = "0x6c6cac790fd0558d28ba8a1d0b52b958eef4387bcffbb38fa0c4663dd07cab70";
        String signaturesResult = this.ethSign(hashStr, 15);
        System.out.println(String.format("signatures: 0x%s", signaturesResult));
    }

    @Test
    public void verifySign() {
        String vHash = "0xcfa64a92cd768be991d4a251df4b109ae381717cc836ac1255e4ffa81ae20bf9";
        String data = "0x31bad7f32438f0ee5f410e729daed9641e777535626c64fc4cfa0b1c428eef9c09dfaa6fc0558b3ae5b183b05f2dad32ddc32f47242c0f2656b3a9fd0031c63f1be6ed178e3102852a9238e8eb55b168a019f33039bb4d92edb69d524529ec05cd7af3b4c6ef30ec223e6076bf108ada540836f0bd741cd29d7a0ae91a68e510851cf7930f3f7b785c4098393be26cf8a2447eb190b7debd7402a343f81ead3e3bbb7117091bab13a91b6c909671fa412dd3190ee4d9ae80f071f6a864c12496d2be1c";
        int times = data.length() / 130;
        int k = 0;
        for (int j=0;j<times;j++) {
            String signed = data.substring(k, k + 130);
            signed = Numeric.cleanHexPrefix(signed);
            if (signed.length() != 130) {
                return;
            }
            String r = "0x" + signed.substring(0, 64);
            String s = "0x" + signed.substring(64, 128);
            ECDSASignature signature = new ECDSASignature(Numeric.decodeQuantity(r), Numeric.decodeQuantity(s));
            byte[] hashBytes = Numeric.hexStringToByteArray(vHash);
            for (int i = 0; i < 4; i++) {
                BigInteger recover = Sign.recoverFromSignature(i, signature, hashBytes);
                if (recover != null) {
                    String address = "0x" + Keys.getAddress(recover);
                    System.out.println(String.format("index: %s, address: %s", i, address));
                }
            }
            k += 130;
        }

    }

    @Test
    public void test() {
        byte n = (byte) 23;
        System.out.println(String.format("%02x", n & 255));
        System.out.println(Numeric.toHexStringNoPrefix(new byte[]{n}));
        String base64 = "KOoEFTB97jAj695UEfVZJtk5oYcroD2om9MhMOa6r3VsF+aZCATTUoc6XKeczWU+pftfuixAgzYR+gZKCWvvQRtF6rhSt8x8WgVT9YC6gXjpoJHjfCy2L3QFK3MypnoQNE5FM5Zgrctcw6Nvi+wLyoD2iyekpSAM93B9swgITyQ3GyeQgsiGhxTuXi0653pUDh4QtRjdm8GuSFMuUI6DBfCSaoTnJPCuDxLQSZLw2HMCfui0vTCz0wtI+JnFq0mQ/yYcTBJoYuAAL9lhX3YaB4gwlSAoxlRR4j6+h06kTs6YAkJkSUk/Vkcv2p9GHFR69KW3QxggHz33MQZHxGZgifp2yhvH5EaWiaWLJ1Qmqs74anqUwSQLpUAvrxeUKxqX/0uGsjt1VgmWo8ZlVTCyICZ8kE5/aOKXRDgRPJBMpWE89HaaG1V1vIxchMaIlolU60U2GRpPPGSDHBbW4T8DA4fSs2J3JKh0cgq4hS+Ukmm5qPYLXwQ2qBHkpUGfbivhxX7coIQby+04KBj1mxybic079IfzzFxiqptDIyzwke/jVK37UV4oCIxLCYN9R5UZfdN9UoxZseB8a2/yDQBlzDLvKmewRhwbv+10HWnHzAY/a0V9D9p79nZtwwd6CS45dv3qBKxVfCSxQF2cdRAs+AUTWeWXHnxi6rG9/E1o7VHfKLjliYsKHCjjDCron0Z4FzkXzaZwAu25Q23oVobb/4mcSrfuw46KOCNLMvZCUpNCg6xr01mIlGvi85/tNEkrqZJc4z5Kj0Ic+ryuopO67bX6iUNCZeN57kL8Mf+n73GUi4r0s06WR2UKi0qVbuXtqOcm3raXmVG2ZJoRTKHu8pALjskWq8DU6xsQQnPFIr9YODvjV/yoKxi/aE3Aj+P4/LjsfNyQDk7DnDq8Fje0Lb3Z18nJKC4sjkN8+RqKVe9/sXT8E8mcXe52HBERERERERERERERERERERERERERERERERERERERERERERERERERERE=";
        System.out.println(Numeric.toHexString(Base64.getDecoder().decode(base64)));
    }

    @Test
    public void arrayTest() {
        String[] arr = new String[]{"aa", "bb", "cc", "dd", "ee", "ff", "gg", "hh", "ii"};
        // aa, "", cc, "", ee
        arr[1] = "";
        arr[3] = "";
        int tempIndex = 0x10;
        for (int i = 0; i < arr.length; i++) {
            String temp = arr[i];
            if ("".equals(temp)) {
                if (tempIndex == 0x10) {
                    tempIndex = i;
                }
                continue;
            } else if (tempIndex != 0x10) {
                arr[tempIndex] = arr[i];
                tempIndex++;
            }
        }
        System.out.println(Arrays.toString(arr));
        System.out.println(0x10);
    }

    @Test
    public void ethDepositByCrossOutTest() throws Exception {
        String directTxHash = "0x176856463c4bf086e5f6df8c600867f5e3d39f6a293bcac188f4bcc61e019b3d";
        Transaction tx = ethWalletApi.getTransactionByHash(directTxHash);
        EthIIParseTxHelper helper = new EthIIParseTxHelper();
        BeanUtilTest.setBean(helper, "ethWalletApi", ethWalletApi);
        EthUnconfirmedTxPo po = new EthUnconfirmedTxPo();
        boolean crossOut = helper.validationEthDepositByCrossOut(tx, po);
        System.out.println(crossOut);
        System.out.println(po.toString());
    }

    @Test
    public void testMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.entrySet().stream().forEach(e -> e.setValue(e.getValue() + 2));
        System.out.println(map);
    }

    @Test
    public void erc20DepositByCrossOutTest() throws Exception {
        // 直接调用erc20合约
        String directTxHash = "0x6abc5a7f2f50e644bb0e75caae0a460d0f8793c19da7b272074784ebee5b8ab5";
        Transaction tx = ethWalletApi.getTransactionByHash(directTxHash);
        EthIIParseTxHelper helper = new EthIIParseTxHelper();
        BeanUtilTest.setBean(helper, "ethWalletApi", ethWalletApi);
        BeanUtilTest.setBean(helper, "ethERC20Helper", new MockEthERC20Helper());
        EthUnconfirmedTxPo po = new EthUnconfirmedTxPo();
        boolean crossOut = helper.validationEthDepositByCrossOut(tx, po);
        System.out.println(crossOut);
        System.out.println(po.toString());
    }

    static class MockEthERC20Helper extends EthERC20Helper {
        @Override
        public boolean isERC20(String address, HeterogeneousTransactionBaseInfo po) {
            return true;
        }
    }
}