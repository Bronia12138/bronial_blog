// Copyright (c) poetize.cn. All rights reserved.
// 习近平：全面加强知识产权保护工作 激发创新活力推动构建新发展格局
// 项目开源版本使用AGPL v3协议，商业活动除非获得商业授权，否则无论以何种方式修改或者使用代码，都需要开源。地址：https://gitee.com/littledokey/poetize.git
// 开源不等于免费，请尊重作者劳动成果。
// 项目闭源版本及资料禁止任何未获得商业授权的网络传播和商业活动。地址：https://poetize.cn/article/20
// 项目唯一官网：https://poetize.cn

package com.ld.poetry.utils.storage;


import com.ld.poetry.handle.PoetryRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 用来处理文件存储，对接多个平台
 */
@Slf4j
@Component
public class FileStorageService implements ApplicationContextAware {

    @Value("${store.type}")
    private String defaultType;

    private final Map<String, StoreService> storeServiceMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, StoreService> consumeService = applicationContext.getBeansOfType(StoreService.class);
        if (!CollectionUtils.isEmpty(consumeService)) {
            for (StoreService value : consumeService.values()) {
                storeServiceMap.put(value.getStoreName(), value);
            }
        }
    }

    /**
     * 获取对应的存储平台，如果存储平台不存在则抛出异常
     */
    public StoreService getFileStorageByStoreType(String storeType) {
        if (!StringUtils.hasText(storeType) || !storeServiceMap.containsKey(storeType)) {
            throw new PoetryRuntimeException("没有找到对应的存储平台：" + storeType);
        }

        return storeServiceMap.get(storeType);
    }

    /**
     * 获取对应的存储平台，如果没有指定则使用默认值
     */
    public StoreService getFileStorage(String storeType) {
        if (!StringUtils.hasText(storeType)) {
            storeType = defaultType;
        }

        if (!storeServiceMap.containsKey(storeType)) {
            throw new PoetryRuntimeException("没有找到对应的存储平台：" + storeType);
        }

        return storeServiceMap.get(storeType);
    }
}
