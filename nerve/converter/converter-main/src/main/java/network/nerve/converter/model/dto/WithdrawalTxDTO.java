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

package network.nerve.converter.model.dto;

import java.math.BigInteger;

/**
 * @author: Loki
 * @date: 2020-03-02
 */
public class WithdrawalTxDTO {
    /**
     * 提现异构链id
     */
//    private int heterogeneousChainId;
    /**
     * 提现资产id
     */
//    private int heterogeneousAssetId;
    /**
     * 提现资产 资产id
     */
    private int assetChainId;
    /**
     * 提现资产 资产id
     */
    private int assetId;
    /**
     * 提现异构链地址
     */
    private String heterogeneousAddress;
    /**
     * 提现金额
     */
    private BigInteger amount;
    /**
     * 交易备注
     */
    private String remark;

    /**
     * 提现发起(签名)地址信息
     */
    private SignAccountDTO signAccount;

    public int getAssetChainId() {
        return assetChainId;
    }

    public void setAssetChainId(int assetChainId) {
        this.assetChainId = assetChainId;
    }

    public int getAssetId() {
        return assetId;
    }

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    //    public int getHeterogeneousChainId() {
//        return heterogeneousChainId;
//    }
//
//    public void setHeterogeneousChainId(int heterogeneousChainId) {
//        this.heterogeneousChainId = heterogeneousChainId;
//    }

    public String getHeterogeneousAddress() {
        return heterogeneousAddress;
    }

    public void setHeterogeneousAddress(String heterogeneousAddress) {
        this.heterogeneousAddress = heterogeneousAddress;
    }

//    public int getHeterogeneousAssetId() {
//        return heterogeneousAssetId;
//    }
//
//    public void setHeterogeneousAssetId(int heterogeneousAssetId) {
//        this.heterogeneousAssetId = heterogeneousAssetId;
//    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }

    public SignAccountDTO getSignAccount() {
        return signAccount;
    }

    public void setSignAccount(SignAccountDTO signAccount) {
        this.signAccount = signAccount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
