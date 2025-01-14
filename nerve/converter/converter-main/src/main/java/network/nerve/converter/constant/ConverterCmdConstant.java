/**
 * MIT License
 * <p>
 * Copyright (c) 2019-2020 nerve.network
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

package network.nerve.converter.constant;

/**
 * @author: Loki
 * @date: 2020-02-27
 */
public interface ConverterCmdConstant {

    String NEW_HASH_SIGN_MESSAGE = "newHashSign";

    String GET_TX_MESSAGE = "getTx";

    String NEW_TX_MESSAGE = "newTx";

    String CHECK_RETRY_PARSE_MESSAGE = "checkRetry";
    String CANCEL_HTG_TX_MESSAGE = "cancelHtgTx";
    String COMPONENT_SIGN = "compSign";

    String GET_HETEROGENEOUS_CHAIN_ASSET_INFO = "cv_get_heterogeneous_chain_asset_info";
    String GET_HETEROGENEOUS_CHAIN_ASSET_INFO_LIST = "cv_get_heterogeneous_chain_asset_info_list";
    String GET_HETEROGENEOUS_CHAIN_ASSET_INFO_BY_ADDRESS = "cv_get_heterogeneous_chain_asset_info_by_address";
    String GET_HETEROGENEOUS_CHAIN_ASSET_INFO_BY_ID = "cv_get_heterogeneous_chain_asset_info_by_id";
    String GET_HETEROGENEOUS_ADDRESS = "cv_get_heterogeneous_address";
    String GET_ALL_HETEROGENEOUS_CHAIN_ASSET_LIST = "cv_get_all_heterogeneous_chain_asset_list";
    String CREATE_HETEROGENEOUS_CONTRACT_ASSET_REG_TX = "cv_create_heterogeneous_contract_asset_reg_pending_tx";
    String VALIDATE_HETEROGENEOUS_CONTRACT_ASSET_REG_TX = "cv_validate_heterogeneous_contract_asset_reg_pending_tx";
    String CREATE_HETEROGENEOUS_MAIN_ASSET_REG_TX = "cv_create_heterogeneous_main_asset_reg_tx";
    String GET_HETEROGENEOUS_REGISTER_NETWORK = "cv_get_heterogeneous_register_network";
    String GET_HETEROGENEOUS_NETWORK_CHAIN_ID = "cv_get_heterogeneous_network_chain_id";

    String VALIDATE_BIND_HETEROGENEOUS_CONTRACT_TOKEN_TO_NERVE_ASSET_REG_TX = "cv_validate_bind_heterogeneous_contract_token_to_nerve_asset_reg_tx";
    String BIND_HETEROGENEOUS_CONTRACT_TOKEN_TO_NERVE_ASSET_REG_TX = "cv_bind_heterogeneous_contract_token_to_nerve_asset_reg_tx";
    String OVERRIDE_BIND_HETEROGENEOUS_CONTRACT_TOKEN_TO_NERVE_ASSET_REG_TX = "cv_override_bind_heterogeneous_contract_token_to_nerve_asset_reg_tx";
    String UNBIND_HETEROGENEOUS_CONTRACT_TOKEN_TO_NERVE_ASSET_REG_TX = "cv_unbind_heterogeneous_contract_token_to_nerve_asset_reg_tx";
    String UNREGISTER_HETEROGENEOUS_CONTRACT_TOKEN_TO_NERVE_ASSET_REG_TX = "cv_unregister_heterogeneous_contract_token_to_nerve_asset_reg_tx";
    /** 提现 */
    String WITHDRAWAL = "cv_withdrawal";
    String WITHDRAWAL_ADDITIONAL_FEE = "cv_withdrawal_additional_fee";
    String VIRTUAL_BANK_INFO = "cv_virtualBankInfo";
    String PROPOSAL = "cv_proposal";
    String BROADCAST_PROPOSAL = "cv_broadcast_proposal";
    String VOTE_PROPOSAL = "cv_voteProposal";
    String RESET_VIRTUAL_BANK = "cv_resetVirtualBank";
    String DISQUALIFICATION = "cv_disqualification";
    String CHECK_RETRY_PARSE = "cv_checkRetryParse";
    String RETRY_WITHDRAWAL = "cv_retry_withdrawal";
    String CANCEL_HTG_TX = "cv_cancelHtgTx";
    String GET_PROPOSAL_INFO = "cv_getProposalInfo";
    String GET_RECHARGE_NERVE_HASH = "cv_getRechargeNerveHash";
    String FIND_BY_WITHDRAWAL_TX_HASH = "cv_findByWithdrawalTxHash";

}
