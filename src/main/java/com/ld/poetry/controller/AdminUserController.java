// Copyright (c) poetize.cn. All rights reserved.
// 习近平：全面加强知识产权保护工作 激发创新活力推动构建新发展格局
// 项目开源版本使用AGPL v3协议，商业活动除非获得商业授权，否则无论以何种方式修改或者使用代码，都需要开源。地址：https://gitee.com/littledokey/poetize.git
// 开源不等于免费，请尊重作者劳动成果。
// 项目闭源版本及资料禁止任何未获得商业授权的网络传播和商业活动。地址：https://poetize.cn/article/20
// 项目唯一官网：https://poetize.cn

package com.ld.poetry.controller;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.poetry.aop.LoginCheck;
import com.ld.poetry.config.PoetryResult;
import com.ld.poetry.constants.CommonConst;
import com.ld.poetry.entity.*;
import com.ld.poetry.enums.CodeMsg;
import com.ld.poetry.enums.PoetryEnum;
import com.ld.poetry.enums.UserLvEnum;
import com.ld.poetry.im.websocket.TioUtil;
import com.ld.poetry.im.websocket.TioWebsocketStarter;
import com.ld.poetry.service.UserService;
import com.ld.poetry.utils.PoetryUtil;
import com.ld.poetry.utils.cache.PoetryCache;
import com.ld.poetry.vo.BaseRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.tio.core.Tio;

/**
 * <p>
 * 后台用户 前端控制器
 * </p>
 *
 * @author sara
 * @since 2021-08-13
 */
@RestController
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 查询用户
     */
    @PostMapping("/user/list")
    @LoginCheck(0)
    public PoetryResult<Page> listUser(@RequestBody BaseRequestVO baseRequestVO) {
        return userService.listUser(baseRequestVO);
    }

    /**
     * 修改用户状态
     * <p>
     * flag = true：解禁
     * flag = false：封禁
     */
    @GetMapping("/user/changeUserStatus")
    @LoginCheck(0)
    public PoetryResult changeUserStatus(@RequestParam("userId") Integer userId, @RequestParam("flag") Boolean flag) {
        if (userId.intValue() == PoetryUtil.getAdminUser().getId().intValue()) {
            return PoetryResult.fail("站长状态不能修改！");
        }

        LambdaUpdateChainWrapper<User> updateChainWrapper = userService.lambdaUpdate().eq(User::getId, userId);
        if (flag) {
            updateChainWrapper.eq(User::getUserStatus, PoetryEnum.STATUS_DISABLE.getCode()).set(User::getUserStatus, PoetryEnum.STATUS_ENABLE.getCode()).update();
        } else {
            updateChainWrapper.eq(User::getUserStatus, PoetryEnum.STATUS_ENABLE.getCode()).set(User::getUserStatus, PoetryEnum.STATUS_DISABLE.getCode()).update();
        }
        logout(userId);
        PoetryCache.remove(CommonConst.USER_CACHE + userId.toString());
        return PoetryResult.success();
    }

    /**
     * 修改用户赞赏
     */
    @GetMapping("/user/changeUserAdmire")
    @LoginCheck(0)
    public PoetryResult changeUserAdmire(@RequestParam("userId") Integer userId, @RequestParam("admire") String admire) {
        userService.lambdaUpdate().eq(User::getId, userId).set(User::getAdmire, StringUtils.hasText(admire) ? admire : null).update();
        logout(userId);
        PoetryCache.remove(CommonConst.ADMIRE);
        PoetryCache.remove(CommonConst.USER_CACHE + userId.toString());
        return PoetryResult.success();
    }

    /**
     * 修改用户类型
     */
    @GetMapping("/user/changeUserType")
    @LoginCheck(0)
    public PoetryResult changeUserType(@RequestParam("userId") Integer userId, @RequestParam("userType") Integer userType) {
        if (userId.intValue() == PoetryUtil.getAdminUser().getId().intValue()) {
            return PoetryResult.fail("站长类型不能修改！");
        }

        if (userType != 0 && userType != 1 && userType != 2) {
            return PoetryResult.fail(CodeMsg.PARAMETER_ERROR);
        }
        userService.lambdaUpdate().eq(User::getId, userId).set(User::getUserType, userType).update();

        logout(userId);
        PoetryCache.remove(CommonConst.USER_CACHE + userId.toString());
        return PoetryResult.success();
    }

    /**
     * 修改用户等级
     */
    @GetMapping("/user/changeUserLv")
    @LoginCheck(0)
    public PoetryResult changeUserLv(@RequestParam("userId") Integer userId, @RequestParam("userLv") Integer userLv) {
        if (userId.intValue() == PoetryUtil.getAdminUser().getId().intValue()) {
            return PoetryResult.fail("站长等级不能修改！");
        }

        if (UserLvEnum.getEnumByCode(userLv) == null) {
            return PoetryResult.fail(CodeMsg.PARAMETER_ERROR);
        }
        userService.lambdaUpdate().eq(User::getId, userId).set(User::getUserLv, userLv).update();

        logout(userId);
        PoetryCache.remove(CommonConst.USER_CACHE + userId.toString());
        return PoetryResult.success();
    }

    private void logout(Integer userId) {
        if (PoetryCache.get(CommonConst.ADMIN_TOKEN + userId) != null) {
            String token = (String) PoetryCache.get(CommonConst.ADMIN_TOKEN + userId);
            PoetryCache.remove(CommonConst.ADMIN_TOKEN + userId);
            PoetryCache.remove(token);
        }

        if (PoetryCache.get(CommonConst.USER_TOKEN + userId) != null) {
            String token = (String) PoetryCache.get(CommonConst.USER_TOKEN + userId);
            PoetryCache.remove(CommonConst.USER_TOKEN + userId);
            PoetryCache.remove(token);
        }
        TioWebsocketStarter tioWebsocketStarter = TioUtil.getTio();
        if (tioWebsocketStarter != null) {
            Tio.removeUser(tioWebsocketStarter.getServerTioConfig(), String.valueOf(userId), "remove user");
        }

    }
}
