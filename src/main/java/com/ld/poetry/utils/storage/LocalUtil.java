// Copyright (c) poetize.cn. All rights reserved.
// 习近平：全面加强知识产权保护工作 激发创新活力推动构建新发展格局
// 项目开源版本使用AGPL v3协议，商业活动除非获得商业授权，否则无论以何种方式修改或者使用代码，都需要开源。地址：https://gitee.com/littledokey/poetize.git
// 开源不等于免费，请尊重作者劳动成果。
// 项目闭源版本及资料禁止任何未获得商业授权的网络传播和商业活动。地址：https://poetize.cn/article/20
// 项目唯一官网：https://poetize.cn

package com.ld.poetry.utils.storage;

import cn.hutool.core.io.FileUtil;
import com.ld.poetry.entity.Resource;
import com.ld.poetry.handle.PoetryRuntimeException;
import com.ld.poetry.service.ResourceService;
import com.ld.poetry.utils.StringUtil;
import com.ld.poetry.vo.FileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "local.enable", havingValue = "true")
public class LocalUtil implements StoreService {

    @Value("${local.uploadUrl}")
    private String uploadUrl;

    @Value("${local.downloadUrl}")
    private String downloadUrl;

    @Autowired
    private ResourceService resourceService;

    @Override
    public void deleteFile(List<String> files) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }

        for (String filePath : files) {
            File file = new File(filePath.replace(downloadUrl, uploadUrl));
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    log.info("文件删除成功：" + filePath);
                    resourceService.lambdaUpdate().eq(Resource::getPath, filePath).remove();
                } else {
                    log.error("文件删除失败：" + filePath);
                }
            } else {
                log.error("文件不存在或者不是一个文件：" + filePath);
            }
        }
    }

    @Override
    public FileVO saveFile(FileVO fileVO) {
        if (!StringUtils.hasText(fileVO.getRelativePath()) ||
                fileVO.getRelativePath().startsWith("/") ||
                fileVO.getRelativePath().endsWith("/")) {
            throw new PoetryRuntimeException("文件路径不合法！");
        }

        String path = fileVO.getRelativePath();
        if (path.contains("/")) {
            String[] split = path.split("/");
            if (split.length > 5) {
                throw new PoetryRuntimeException("文件路径不合法！");
            }
            for (int i = 0; i < split.length - 1; i++) {
                if (!StringUtil.isValidDirectoryName(split[i])) {
                    throw new PoetryRuntimeException("文件路径不合法！");
                }
            }
            if (!StringUtil.isValidFileName(split[split.length - 1])) {
                throw new PoetryRuntimeException("文件路径不合法！");
            }
        }
        String absolutePath = uploadUrl + path;
        if (FileUtil.exist(absolutePath)) {
            throw new PoetryRuntimeException("文件已存在！");
        }
        try {
            File newFile = FileUtil.touch(absolutePath);
            fileVO.getFile().transferTo(newFile);
            FileVO result = new FileVO();
            result.setAbsolutePath(absolutePath);
            result.setVisitPath(downloadUrl + path);
            return result;
        } catch (IOException e) {
            log.error("文件上传失败：", e);
            FileUtil.del(absolutePath);
            throw new PoetryRuntimeException("文件上传失败！");
        }
    }

    @Override
    public String getStoreName() {
        return StoreEnum.LOCAL.getCode();
    }
}
