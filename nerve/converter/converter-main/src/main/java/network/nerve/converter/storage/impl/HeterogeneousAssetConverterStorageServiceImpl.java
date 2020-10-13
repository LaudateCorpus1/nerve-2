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
package network.nerve.converter.storage.impl;


import io.nuls.core.core.annotation.Autowired;
import io.nuls.core.core.annotation.Component;
import io.nuls.core.model.ByteUtils;
import io.nuls.core.rockdb.service.RocksDBService;
import network.nerve.converter.config.ConverterConfig;
import network.nerve.converter.constant.ConverterDBConstant;
import network.nerve.converter.model.bo.HeterogeneousAssetInfo;
import network.nerve.converter.model.bo.NerveAssetInfo;
import network.nerve.converter.storage.HeterogeneousAssetConverterStorageService;
import network.nerve.converter.utils.ConverterDBUtil;

import java.util.Arrays;

import static network.nerve.converter.utils.ConverterDBUtil.stringToBytes;


/**
 * @desription:
 * @author: Mimi
 * @date: 2018/5/24
 */
@Component
public class HeterogeneousAssetConverterStorageServiceImpl implements HeterogeneousAssetConverterStorageService {

    @Autowired
    private ConverterConfig converterConfig;

    private final String baseArea = ConverterDBConstant.DB_HETEROGENEOUS_CHAIN_INFO;
    private final String KEY_PREFIX_NERVE = "HETEROGENEOUS_ASSET_NERVE-";
    private final String KEY_PREFIX_H = "HETEROGENEOUS_ASSET_H-";
    private final String CONTACT_LINE = "-";

    @Override
    public int saveAssetInfo(int nerveAssetChainId, int nerveAssetId, HeterogeneousAssetInfo info) throws Exception {
        if (info == null) {
            return 0;
        }
        if (converterConfig.getChainId() == nerveAssetChainId) {
            ConverterDBUtil.putModel(baseArea, stringToBytes(KEY_PREFIX_NERVE + nerveAssetId), info);
            RocksDBService.put(baseArea, stringToBytes(KEY_PREFIX_H + info.getChainId() + CONTACT_LINE + info.getAssetId()), ByteUtils.intToBytes(nerveAssetId));
        } else {
            ConverterDBUtil.putModel(baseArea, stringToBytes(KEY_PREFIX_NERVE + nerveAssetChainId + CONTACT_LINE + nerveAssetId), info);
            ConverterDBUtil.putModel(baseArea, stringToBytes(KEY_PREFIX_H + info.getChainId() + CONTACT_LINE + info.getAssetId()), new NerveAssetInfo(nerveAssetChainId, nerveAssetId));
        }
        return 1;
    }

    public int deleteAssetInfoByNerveAssetId(int nerveAssetChainId, int nerveAssetId) throws Exception {
        HeterogeneousAssetInfo info = this.getHeterogeneousAssetInfo(nerveAssetChainId, nerveAssetId);
        if (info != null) {
            if (converterConfig.getChainId() == nerveAssetChainId) {
                RocksDBService.delete(baseArea, stringToBytes(KEY_PREFIX_NERVE + nerveAssetId));
            } else {
                RocksDBService.delete(baseArea, stringToBytes(KEY_PREFIX_NERVE + nerveAssetChainId + CONTACT_LINE + nerveAssetId));
            }
            RocksDBService.delete(baseArea, stringToBytes(KEY_PREFIX_H + info.getChainId() + CONTACT_LINE + info.getAssetId()));
            return 1;
        }
        return 0;
    }

    @Override
    public int deleteAssetInfo(int heterogeneousChainId, int heterogeneousAssetId) throws Exception {
        NerveAssetInfo info = this.getNerveAssetInfo(heterogeneousChainId, heterogeneousAssetId);
        if (converterConfig.getChainId() == info.getAssetChainId()) {
            RocksDBService.delete(baseArea, stringToBytes(KEY_PREFIX_NERVE + info.getAssetId()));
        } else {
            RocksDBService.delete(baseArea, stringToBytes(KEY_PREFIX_NERVE + info.getAssetChainId() + CONTACT_LINE + info.getAssetId()));
        }
        RocksDBService.delete(baseArea, stringToBytes(KEY_PREFIX_H + heterogeneousChainId + CONTACT_LINE + heterogeneousAssetId));
        return 1;
    }

    @Override
    public HeterogeneousAssetInfo getHeterogeneousAssetInfo(int nerveAssetChainId, int nerveAssetId) {
        HeterogeneousAssetInfo info;
        if (converterConfig.getChainId() == nerveAssetChainId) {
            info = ConverterDBUtil.getModel(baseArea, stringToBytes(KEY_PREFIX_NERVE + nerveAssetId), HeterogeneousAssetInfo.class);
        } else {
            info = ConverterDBUtil.getModel(baseArea, stringToBytes(KEY_PREFIX_NERVE + nerveAssetChainId + CONTACT_LINE + nerveAssetId), HeterogeneousAssetInfo.class);
        }
        return info;
    }

    @Override
    public NerveAssetInfo getNerveAssetInfo(int heterogeneousChainId, int heterogeneousAssetId) {
        byte[] bytes = RocksDBService.get(baseArea, stringToBytes(KEY_PREFIX_H + heterogeneousChainId + CONTACT_LINE + heterogeneousAssetId));
        if (bytes == null) {
            return NerveAssetInfo.emptyInstance();
        }
        NerveAssetInfo info;
        if (bytes.length > 4) {
            info = ConverterDBUtil.getModel(bytes, NerveAssetInfo.class);
        } else {
            int assetId = ByteUtils.bytesToInt(bytes);
            info = new NerveAssetInfo(converterConfig.getChainId(), assetId);
        }
        return info;
    }

}
