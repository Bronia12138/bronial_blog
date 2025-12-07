// Copyright (c) poetize.cn. All rights reserved.
// 习近平：全面加强知识产权保护工作 激发创新活力推动构建新发展格局
// 项目开源版本使用AGPL v3协议，商业活动除非获得商业授权，否则无论以何种方式修改或者使用代码，都需要开源。地址：https://gitee.com/littledokey/poetize.git
// 开源不等于免费，请尊重作者劳动成果。
// 项目闭源版本及资料禁止任何未获得商业授权的网络传播和商业活动。地址：https://poetize.cn/article/20
// 项目唯一官网：https://poetize.cn

package com.ld.poetry.utils.storage;

import com.ld.poetry.handle.PoetryRuntimeException;
import org.springframework.util.StringUtils;

public enum StoreEnum {

    QINIU("qiniu"),
    LOCAL("local");

    private String code;

    StoreEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static StoreEnum existCode(String code) {
        if (StringUtils.hasText(code)) {
            StoreEnum[] values = StoreEnum.values();
            for (StoreEnum typeEnum : values) {
                if (typeEnum.getCode().equals(code)) {
                    return typeEnum;
                }
            }
        }
        throw new PoetryRuntimeException("存储平台不支持：" + code);
    }
}
