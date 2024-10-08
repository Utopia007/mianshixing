package com.luyouxiao.mianshixing.utils;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

/**
 * @author 鹿又笑
 * @create 2024/10/8-8:16
 * @description
 */
@Slf4j
public class BlackIpUtils {

    private static BitMapBloomFilter bloomFilter;

    // 判断是否为黑名单IP
    public static boolean isBlackIp(String ip) {
        return bloomFilter.contains(ip);
    }

    // 重建IP黑名单
    public static void rebuildBlackIpList(String configInfo) {
        if (StrUtil.isBlank(configInfo)) {
            configInfo = "{}";
        }
        // 解析yaml文件
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(configInfo, Map.class);
        // 获取IP黑名单
        List<String> blackIPList = (List<String>) map.get("blackIpList");
        synchronized (BlackIpUtils.class) {
            if (CollUtil.isNotEmpty(blackIPList)) {
                BitMapBloomFilter bitMapBloomFilter = new BitMapBloomFilter(105);
                for (String ip : blackIPList) {
                    bitMapBloomFilter.add(ip);
                }
                bloomFilter = bitMapBloomFilter;
            } else {
                bloomFilter = new BitMapBloomFilter(100);
            }
        }
    }

}
