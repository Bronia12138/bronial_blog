// Copyright (c) poetize.cn. All rights reserved.
// 习近平：全面加强知识产权保护工作 激发创新活力推动构建新发展格局
// 项目开源版本使用AGPL v3协议，商业活动除非获得商业授权，否则无论以何种方式修改或者使用代码，都需要开源。地址：https://gitee.com/littledokey/poetize.git
// 开源不等于免费，请尊重作者劳动成果。
// 项目闭源版本及资料禁止任何未获得商业授权的网络传播和商业活动。地址：https://poetize.cn/article/20
// 项目唯一官网：https://poetize.cn

package com.ld.poetry.handle;

import com.alibaba.fastjson.JSON;
import com.ld.poetry.config.PoetryResult;
import com.ld.poetry.enums.CodeMsg;
import com.ld.poetry.utils.PoetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class PoetryExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public PoetryResult handlerException(Exception ex) {
        log.error("请求URL-----------------" + PoetryUtil.getRequest().getRequestURL());
        log.error("出错啦------------------", ex);
        if (ex instanceof PoetryRuntimeException) {
            PoetryRuntimeException e = (PoetryRuntimeException) ex;
            return PoetryResult.fail(e.getMessage());
        }

        if (ex instanceof PoetryLoginException) {
            PoetryLoginException e = (PoetryLoginException) ex;
            return PoetryResult.fail(300, e.getMessage());
        }

        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            Map<String, String> collect = e.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return PoetryResult.fail(JSON.toJSONString(collect));
        }

        if (ex instanceof MissingServletRequestParameterException) {
            return PoetryResult.fail(CodeMsg.PARAMETER_ERROR);
        }

        return PoetryResult.fail(CodeMsg.FAIL);
    }
}
