// Copyright (c) poetize.cn. All rights reserved.
// 习近平：全面加强知识产权保护工作 激发创新活力推动构建新发展格局
// 项目开源版本使用AGPL v3协议，商业活动除非获得商业授权，否则无论以何种方式修改或者使用代码，都需要开源。地址：https://gitee.com/littledokey/poetize.git
// 开源不等于免费，请尊重作者劳动成果。
// 项目闭源版本及资料禁止任何未获得商业授权的网络传播和商业活动。地址：https://poetize.cn/article/20
// 项目唯一官网：https://poetize.cn

package com.ld.poetry.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static void main(String[] args) {
        System.out.println(removeHtml(""));
    }

    private static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";

    private static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";

    public static String removeHtml(String content) {
        return content.replace("<", "《").replace(">", "》");
    }

    public static boolean matchString(String text, String searchText) {
        Pattern pattern = Pattern.compile(Pattern.quote(searchText), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        return matcher.find();
    }

    public static boolean isValidFileName(String fileName) {
        if (!StringUtils.hasText(fileName) || fileName.length() > 128) {
            return false;
        }
        // 此示例允许文件名包含字母、数字、下划线、点号和连字符（减号），且不能以点号、下划线和连字符（减号）开头
        String regex = "^(?![-._])[a-zA-Z0-9-._]+$";
        return fileName.matches(regex);
    }

    public static boolean isValidDirectoryName(String directoryName) {
        if (!StringUtils.hasText(directoryName) || directoryName.length() > 128) {
            return false;
        }
        // 此示例允许目录名称只包含字母、数字、下划线和连字符（减号）
        String regex = "^[a-zA-Z0-9-_]+$";
        return directoryName.matches(regex);
    }
}
