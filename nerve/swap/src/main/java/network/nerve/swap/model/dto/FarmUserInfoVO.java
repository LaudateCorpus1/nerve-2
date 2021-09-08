package network.nerve.swap.model.dto;

import io.nuls.core.rpc.model.ApiModel;
import io.nuls.core.rpc.model.ApiModelProperty;

/**
 * @author Niels
 */
@ApiModel(name = "质押信息详情")
public class FarmUserInfoVO {
    @ApiModelProperty(description = "Farm-HASH")
    private String farmHash;
    @ApiModelProperty(description = "user address")
    private String userAddress;
    @ApiModelProperty(description = "已质押金额")
    private String amount;
    @ApiModelProperty(description = "待领取奖励金额")
    private String reward;

    public String getFarmHash() {
        return farmHash;
    }

    public void setFarmHash(String farmHash) {
        this.farmHash = farmHash;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }
}
