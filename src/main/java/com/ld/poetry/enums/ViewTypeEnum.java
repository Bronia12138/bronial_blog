package com.ld.poetry.enums;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 访问类型
 */
public enum ViewTypeEnum {

    VIEW_TYPE_PASSWORD("password", "密码"),
    VIEW_TYPE_USER_LV("userLv", "用户等级"),
    VIEW_TYPE_USER_LOGIN("login", "登录"),
    VIEW_TYPE_PUBLIC("public", "公开");

    private final String code;

    private final String desc;

    ViewTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ViewTypeEnum getEnumByCode(String code) {
        if (StringUtils.hasText(code)) {
            ViewTypeEnum[] values = ViewTypeEnum.values();
            for (ViewTypeEnum typeEnum : values) {
                if (typeEnum.getCode().equalsIgnoreCase(code)) {
                    return typeEnum;
                }
            }
        }
        return null;
    }

    public static ViewTypeEnum getEnumByDesc(String desc) {
        if (StringUtils.hasText(desc)) {
            ViewTypeEnum[] values = ViewTypeEnum.values();
            for (ViewTypeEnum typeEnum : values) {
                if (typeEnum.getDesc().equalsIgnoreCase(desc)) {
                    return typeEnum;
                }
            }
        }
        return null;
    }

    public static Map<String, String> getEnumMap() {
        return Arrays.stream(ViewTypeEnum.values()).collect(Collectors.toMap(ViewTypeEnum::getCode, ViewTypeEnum::getDesc));
    }
}
