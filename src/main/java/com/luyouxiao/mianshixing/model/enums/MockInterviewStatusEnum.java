package com.luyouxiao.mianshixing.model.enums;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 鹿又笑
 * @create 2025/4/9-20:43
 * @description
 */
public enum MockInterviewStatusEnum {

    TO_START("待开始", 0),
    IN_PROGRESS("进行中", 1),
    ENDED("已结束", 2);

    private String text;
    private int value;

    MockInterviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取所有值列表
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 值获取枚举
     * @param value
     * @return
     */
    public static MockInterviewStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (MockInterviewStatusEnum mockInterviewStatusEnum : MockInterviewStatusEnum.values()) {
            if (mockInterviewStatusEnum.value == value) {
                return mockInterviewStatusEnum;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public int getValue() {
        return value;
    }
}
