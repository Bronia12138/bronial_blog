// Copyright (c) poetize.cn. All rights reserved.
// 习近平：全面加强知识产权保护工作 激发创新活力推动构建新发展格局
// 项目开源版本使用AGPL v3协议，商业活动除非获得商业授权，否则无论以何种方式修改或者使用代码，都需要开源。地址：https://gitee.com/littledokey/poetize.git
// 开源不等于免费，请尊重作者劳动成果。
// 项目闭源版本及资料禁止任何未获得商业授权的网络传播和商业活动。地址：https://poetize.cn/article/20
// 项目唯一官网：https://poetize.cn

package com.ld.poetry.config;

import com.ld.poetry.enums.CodeMsg;
import lombok.Data;

import java.io.Serializable;

@Data
public class PoetryResult<T> implements Serializable {

    private static final long serialVersionUI = 1L;

    private int code;
    private String message;
    private T data;
    private long currentTimeMillis = System.currentTimeMillis();

    public PoetryResult() {
        this.code = 200;
    }

    public PoetryResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public PoetryResult(T data) {
        this.code = 200;
        this.data = data;
    }

    public PoetryResult(String message) {
        this.code = 500;
        this.message = message;
    }

    public static <T> PoetryResult<T> fail(String message) {
        return new PoetryResult(message);
    }

    public static <T> PoetryResult<T> fail(CodeMsg codeMsg) {
        return new PoetryResult(codeMsg.getCode(), codeMsg.getMsg());
    }

    public static <T> PoetryResult<T> fail(Integer code, String message) {
        return new PoetryResult(code, message);
    }

    public static <T> PoetryResult<T> success(T data) {
        return new PoetryResult(data);
    }

    public static <T> PoetryResult<T> success() {
        return new PoetryResult();
    }
}
