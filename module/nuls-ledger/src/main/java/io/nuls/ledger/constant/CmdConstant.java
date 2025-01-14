/*
 * MIT License
 *
 * Copyright (c) 2017-2019 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package io.nuls.ledger.constant;

/**
 * @author lan
 * @description
 * @date 2019/03/10
 **/
public interface CmdConstant {
    /*CALL cmd */
    /**
     * 根据区块高度获取区块
     */
    String CMD_GET_BLOCK_BY_HEIGHT = "getBlockByHeight";

    /**
     * 获取最新高度
     */
    String CMD_LATEST_HEIGHT = "latestHeight";
    /**
     * 账户签名校验
     */
    String CMD_AC_SIGN_DIGEST = "ac_signDigest";
    /**
     * 发起新交易接口
     */
    String CMD_TX_NEW = "tx_newTx";

    /*RPC CMD*/


    String CMD_CHAIN_ASSET_REG_INFO = "getAssetRegInfo";
    String CMD_CHAIN_ASSET_REG_INFO_BY_HASH = "getAssetRegInfoByHash";
    String CMD_CHAIN_ASSET_REG_INFO_BY_ASSETID = "getAssetRegInfoByAssetId";
    String CMD_CHAIN_ASSET_TX_REG = "chainAssetTxReg";

    String CMD_CHAIN_ASSET_CONTRACT_REG = "chainAssetContractReg";
    String CMD_CHAIN_ASSET_CONTRACT_ROLL_BACK = "chainAssetContractRollBack";
    String CMD_CHAIN_ASSET_CONTRACT_ADDRESS = "getAssetContractAddress";
    String CMD_CHAIN_ASSET_CONTRACT_ASSETID = "getAssetContractAssetId";
    String CMD_CHAIN_ASSET_CONTRACT = "getAssetContract";

    String CMD_CROSS_CHAIN_ASSET_REG = "lg_cross_chain_asset_reg";
    String CMD_CROSS_CHAIN_ASSET_LIST_REG = "lg_cross_chain_asset_list_reg";
    String CMD_CROSS_CHAIN_ASSET_DELETE = "lg_cross_chain_asset_delete";
    String CMD_GET_CROSS_CHAIN_ASSET = "lg_get_cross_chain_asset";
    String CMD_GET_ALL_CROSS_CHAIN_ASSET = "lg_get_all_cross_chain_asset";
    String CMD_GET_ASSET = "lg_get_asset";
    String CMD_GET_ALL_ASSET = "lg_get_all_asset";
    String CMD_CHAIN_ASSET_HETEROGENEOUS_REG = "lg_chain_asset_heterogeneous_reg";
    String CMD_CHAIN_ASSET_HETEROGENEOUS_ROLLBACK = "lg_chain_asset_heterogeneous_rollback";
    String CMD_BIND_HETEROGENEOUS_ASSET_REG = "lg_bind_heterogeneous_asset_reg";
    String CMD_UNBIND_HETEROGENEOUS_ASSET_REG = "lg_unbind_heterogeneous_asset_reg";

    String CMD_CHAIN_ASSET_SWAP_LIQUIDITY_POOL_REG = "lg_chain_asset_swap_liquidity_pool_reg";
    String CMD_CHAIN_ASSET_SWAP_LIQUIDITY_POOL_ROLLBACK = "lg_chain_asset_swap_liquidity_pool_rollback";

    /**
     * 获取确认交易余额
     */
    String CMD_GET_BALANCE = "getBalance";


    /**
     * 获取含未确认交易的信息
     */
    String CMD_GET_BALANCE_NONCE = "getBalanceNonce";
    /**
     * 获取含未确认交易的信息的集合
     */
    String CMD_GET_BALANCE_LIST = "getBalanceList";
    /**
     * 获取账户nonce值
     */
    String CMD_GET_NONCE = "getNonce";
    /**
     * 获取冻结列表
     */
    String CMD_GET_FREEZE_LIST = "getFreezeList";
    /**
     * 提交未确认交易
     */
    String CMD_COMMIT_UNCONFIRMED_TX = "commitUnconfirmedTx";
    /**
     * 批量提交未确认交易
     */
    String CMD_COMMIT_UNCONFIRMED_TXS = "commitBatchUnconfirmedTxs";
    /**
     * 回滚未确认交易
     */
    String CMD_ROLLBACK_UNCONFIRMED_TX = "rollBackUnconfirmTx";

    /**
     * 回滚未确认交易
     */
    String CMD_CLEAR_UNCONFIRMED_TXS = "clearUnconfirmTxs";


    /**
     * 提交区块交易
     */
    String CMD_COMMIT_BLOCK_TXS = "commitBlockTxs";

    /**
     * 回滚区块交易
     */
    String CMD_ROLLBACK_BLOCK_TXS = "rollBackBlockTxs";

    /**
     * 区块打包整体校验
     */
    String CMD_VERIFY_COINDATA_PACKAGED = "verifyCoinDataPackaged";

    /**
     * 区块打包整体校验
     */
    String CMD_VERIFY_COINDATA_BATCH_PACKAGED = "verifyCoinDataBatchPackaged";

    /**
     * 单笔交易校验
     */
    String CMD_VERIFY_COINDATA = "verifyCoinData";

    /**
     * 回滚打包交易的状态
     */
    String CMD_ROLLBACKTX_VALIDATE_STATUS = "rollbackTxValidateStatus";
    /**
     * 批量校验开始
     */
    String CMD_BATCH_VALIDATE_BEGIN = "batchValidateBegin";

    /**
     * 整区块校验
     */
    String CMD_BLOCK_VALIDATE = "blockValidate";
    /**
     * 获取资产信息
     */
    String CMD_GET_ASSETS_BY_ID = "getAssetsById";


}
