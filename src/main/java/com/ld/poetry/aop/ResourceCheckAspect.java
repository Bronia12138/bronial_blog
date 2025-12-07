package com.ld.poetry.aop;

import com.ld.poetry.constants.CommonConst;
import com.ld.poetry.entity.Article;
import com.ld.poetry.entity.User;
import com.ld.poetry.enums.CodeMsg;
import com.ld.poetry.enums.ViewTypeEnum;
import com.ld.poetry.handle.PoetryLoginException;
import com.ld.poetry.handle.PoetryRuntimeException;
import com.ld.poetry.utils.*;
import com.ld.poetry.utils.cache.PoetryCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Aspect
@Component
@Order(2)
@Slf4j
public class ResourceCheckAspect {

    @Autowired
    private CommonQuery commonQuery;

    @Around("@annotation(resourceCheck)")
    public Object around(ProceedingJoinPoint joinPoint, ResourceCheck resourceCheck) throws Throwable {
        if (CommonConst.RESOURCE_ARTICLE_DOC.equals(resourceCheck.value())) {
            Object[] args = joinPoint.getArgs();
            Integer id = (Integer) args[0];
            Article articleInfo = commonQuery.getArticleInfo(id);
            if (articleInfo == null) {
                throw new PoetryRuntimeException("文章不存在！");
            }
            if (!ViewTypeEnum.VIEW_TYPE_PUBLIC.getCode().equals(articleInfo.getViewType())) {
                String token = PoetryUtil.getToken();
                if (!StringUtils.hasText(token)) {
                    throw new PoetryRuntimeException("该文章是加密文章，请先登录！");
                }
                User user = (User) PoetryCache.get(token);

                if (user == null) {
                    throw new PoetryLoginException(CodeMsg.LOGIN_EXPIRED.getMsg());
                }

                String tips = StringUtils.hasText(articleInfo.getTips()) ? articleInfo.getTips() : "您未获得文章访问权限，请联系作者！";

                if (ViewTypeEnum.VIEW_TYPE_USER_LV.getCode().equals(articleInfo.getViewType())) {
                    int need = Integer.parseInt(articleInfo.getViewValue());
                    int current = user.getUserLv().intValue();
                    if (current >= need) {
                        return joinPoint.proceed();
                    }
                    throw new PoetryRuntimeException(tips);
                } else if (ViewTypeEnum.VIEW_TYPE_PASSWORD.getCode().equals(articleInfo.getViewType())) {
                    if (args[1] != null) {
                        String current = (String) args[1];
                        String need = articleInfo.getViewValue();
                        if (current.equals(need)) {
                            return joinPoint.proceed();
                        }
                    }
                    throw new PoetryRuntimeException("密码错误" + tips);
                } else {
                    return joinPoint.proceed();
                }
            } else {
                return joinPoint.proceed();
            }
        } else {
            return joinPoint.proceed();
        }
    }
}
