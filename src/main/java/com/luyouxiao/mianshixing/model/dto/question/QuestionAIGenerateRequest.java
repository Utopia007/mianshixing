package com.luyouxiao.mianshixing.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 鹿又笑
 * @create 2025/4/4-22:46
 * @description
 */
@Data
public class QuestionAIGenerateRequest implements Serializable {

    // 题目类型
    private String questionType;

    // 题目数量
    private int number;
}
