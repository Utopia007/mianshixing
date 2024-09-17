package com.luyouxiao.mianshixing.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 鹿又笑
 * @create 2024/9/16-22:17
 * @description
 */
@Data
public class QuestionBankQuestionRemoveRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;

}
