package network.nerve.converter.heterogeneouschain.bnb.model;

import java.io.Serializable;

/**
 * 同步区块的高度
 *
 * @author wangkun23
 */
public class BnbSimpleBlockHeader implements Serializable {

    private Long height;
    private String hash;
    private String preHash;
    private Long createTime;

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}