package com.ld.poetry.enums;


import org.springframework.util.StringUtils;

/**
 * 用户等级
 */
public enum UserLvEnum {

    LV1(1, "LV1"),
    LV2(2, "LV2"),
    LV3(3, "LV3"),
    LV4(4, "LV4"),
    LV5(5, "LV5"),
    LV6(6, "LV6");

    private final int code;

    private final String desc;

    UserLvEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UserLvEnum getEnumByCode(Integer code) {
        if (code != null) {
            UserLvEnum[] values = UserLvEnum.values();
            for (UserLvEnum typeEnum : values) {
                if (typeEnum.getCode() == code.intValue()) {
                    return typeEnum;
                }
            }
        }
        return null;
    }

    public static UserLvEnum getEnumByDesc(String desc) {
        if (StringUtils.hasText(desc)) {
            UserLvEnum[] values = UserLvEnum.values();
            for (UserLvEnum typeEnum : values) {
                if (typeEnum.getDesc().equalsIgnoreCase(desc)) {
                    return typeEnum;
                }
            }
        }
        return null;
    }
}
