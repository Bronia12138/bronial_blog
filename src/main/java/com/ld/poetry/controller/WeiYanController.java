// Copyright (c) poetize.cn. All rights reserved.
// 习近平：全面加强知识产权保护工作 激发创新活力推动构建新发展格局
// 项目开源版本使用AGPL v3协议，商业活动除非获得商业授权，否则无论以何种方式修改或者使用代码，都需要开源。地址：https://gitee.com/littledokey/poetize.git
// 开源不等于免费，请尊重作者劳动成果。
// 项目闭源版本及资料禁止任何未获得商业授权的网络传播和商业活动。地址：https://poetize.cn/article/20
// 项目唯一官网：https://poetize.cn

package com.ld.poetry.controller;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ld.poetry.aop.LoginCheck;
import com.ld.poetry.config.PoetryResult;
import com.ld.poetry.aop.SaveCheck;
import com.ld.poetry.dao.ArticleMapper;
import com.ld.poetry.entity.Article;
import com.ld.poetry.entity.Comment;
import com.ld.poetry.entity.User;
import com.ld.poetry.entity.WeiYan;
import com.ld.poetry.enums.CommentTypeEnum;
import com.ld.poetry.service.CommentService;
import com.ld.poetry.service.WeiYanService;
import com.ld.poetry.constants.CommonConst;
import com.ld.poetry.enums.PoetryEnum;
import com.ld.poetry.utils.CommonQuery;
import com.ld.poetry.utils.LvUtil;
import com.ld.poetry.utils.PoetryUtil;
import com.ld.poetry.utils.StringUtil;
import com.ld.poetry.vo.BaseRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 微言表 前端控制器
 * </p>
 *
 * @author sara
 * @since 2021-10-26
 */
@RestController
@RequestMapping("/weiYan")
public class WeiYanController {

    @Autowired
    private WeiYanService weiYanService;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommonQuery commonQuery;

    /**
     * 保存
     */
    @PostMapping("/saveWeiYan")
    @LoginCheck
    @SaveCheck
    public PoetryResult saveWeiYan(@RequestBody WeiYan weiYanVO) {
        if (!StringUtils.hasText(weiYanVO.getContent())) {
            return PoetryResult.fail("微言不能为空！");
        }

        String content = StringUtil.removeHtml(weiYanVO.getContent());
        if (!StringUtils.hasText(content)) {
            return PoetryResult.fail("微言内容不合法！");
        }
        weiYanVO.setContent(content);

        WeiYan weiYan = new WeiYan();
        weiYan.setUserId(PoetryUtil.getUserId());
        weiYan.setContent(weiYanVO.getContent());
        weiYan.setIsPublic(weiYanVO.getIsPublic());
        weiYan.setType(CommonConst.WEIYAN_TYPE_FRIEND);
        weiYanService.save(weiYan);
        return PoetryResult.success();
    }


    /**
     * 保存
     */
    @PostMapping("/saveNews")
    @LoginCheck
    public PoetryResult saveNews(@RequestBody WeiYan weiYanVO) {
        if (!StringUtils.hasText(weiYanVO.getContent()) || weiYanVO.getSource() == null) {
            return PoetryResult.fail("信息不全！");
        }

        if (weiYanVO.getCreateTime() == null) {
            weiYanVO.setCreateTime(LocalDateTime.now());
        }

        Integer userId = PoetryUtil.getUserId();

        LambdaQueryChainWrapper<Article> wrapper = new LambdaQueryChainWrapper<>(articleMapper);
        Integer count = wrapper.eq(Article::getId, weiYanVO.getSource()).eq(Article::getUserId, userId).count();

        if (count == null || count < 1) {
            return PoetryResult.fail("来源不存在！");
        }

        WeiYan weiYan = new WeiYan();
        weiYan.setUserId(userId);
        weiYan.setContent(weiYanVO.getContent());
        weiYan.setIsPublic(Boolean.TRUE);
        weiYan.setSource(weiYanVO.getSource());
        weiYan.setCreateTime(weiYanVO.getCreateTime());
        weiYan.setType(CommonConst.WEIYAN_TYPE_NEWS);
        weiYanService.save(weiYan);
        return PoetryResult.success();
    }

    /**
     * 查询List
     */
    @PostMapping("/listNews")
    public PoetryResult<BaseRequestVO> listNews(@RequestBody BaseRequestVO baseRequestVO) {
        if (baseRequestVO.getSource() == null) {
            return PoetryResult.fail("来源不能为空！");
        }
        LambdaQueryChainWrapper<WeiYan> lambdaQuery = weiYanService.lambdaQuery();
        lambdaQuery.eq(WeiYan::getType, CommonConst.WEIYAN_TYPE_NEWS);
        lambdaQuery.eq(WeiYan::getSource, baseRequestVO.getSource());
        lambdaQuery.eq(WeiYan::getIsPublic, PoetryEnum.PUBLIC.getCode());

        lambdaQuery.orderByDesc(WeiYan::getCreateTime).page(baseRequestVO);
        return PoetryResult.success(baseRequestVO);
    }

    /**
     * 删除
     */
    @GetMapping("/deleteWeiYan")
    @LoginCheck
    public PoetryResult deleteWeiYan(@RequestParam("id") Integer id) {
        Integer userId = PoetryUtil.getUserId();
        boolean remove = weiYanService.lambdaUpdate().eq(WeiYan::getId, id)
                .eq(WeiYan::getUserId, userId)
                .remove();
        if (remove) {
            commentService.lambdaUpdate().eq(Comment::getSource, id)
                    .eq(Comment::getType, CommentTypeEnum.COMMENT_TYPE_JOTTING.getCode())
                    .remove();
        }
        return PoetryResult.success();
    }


    /**
     * 查询List
     */
    @PostMapping("/listWeiYan")
    public PoetryResult<BaseRequestVO> listWeiYan(@RequestBody BaseRequestVO baseRequestVO) {
        LambdaQueryChainWrapper<WeiYan> lambdaQuery = weiYanService.lambdaQuery();
        lambdaQuery.eq(WeiYan::getType, CommonConst.WEIYAN_TYPE_FRIEND);
        if (baseRequestVO.getUserId() == null) {
            if (PoetryUtil.getUserId() != null) {
                lambdaQuery.eq(WeiYan::getUserId, PoetryUtil.getUserId());
            } else {
                lambdaQuery.eq(WeiYan::getIsPublic, PoetryEnum.PUBLIC.getCode());
                lambdaQuery.eq(WeiYan::getUserId, PoetryUtil.getAdminUser().getId());
            }
        } else {
            if (!baseRequestVO.getUserId().equals(PoetryUtil.getUserId())) {
                lambdaQuery.eq(WeiYan::getIsPublic, PoetryEnum.PUBLIC.getCode());
            }
            lambdaQuery.eq(WeiYan::getUserId, baseRequestVO.getUserId());
        }

        lambdaQuery.orderByDesc(WeiYan::getCreateTime).page(baseRequestVO);

        if (!CollectionUtils.isEmpty(baseRequestVO.getRecords())) {
            List<WeiYan> records = baseRequestVO.getRecords();
            records.forEach(w -> {
                w.setCommentCount(commonQuery.getCommentCount(w.getId(), CommentTypeEnum.COMMENT_TYPE_JOTTING.getCode()));
                User user = commonQuery.getUser(w.getUserId());
                if (user != null) {
                    w.setAvatar(user.getAvatar());
                    w.setUsername(user.getUsername());
                    w.setCreateTimeLv(LvUtil.getCreateTimeLv(user.getCreateTime(), LocalDateTime.now(), user.getId()));
                }
            });
        }
        return PoetryResult.success(baseRequestVO);
    }
}
