package com.luyouxiao.mianshixing.model.enums;

import cn.hutool.core.util.ObjectUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 鹿又笑
 * @create 2025/4/9-23:07
 * @description
 */
public enum MockInterviewEnum {

    START("开始", "start"),
    CHAT("聊天", "chat"),
    END("结束", "end");

    // 事件描述
    private String text;
    // 事件值
    private String value;

    MockInterviewEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static MockInterviewEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (MockInterviewEnum mockInterviewEnum : MockInterviewEnum.values()) {
            if (mockInterviewEnum.value.equals(value)) {
                return mockInterviewEnum;
            }
        }
        return null;
    }

    /**
     * 获取所有事件的值列表
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
