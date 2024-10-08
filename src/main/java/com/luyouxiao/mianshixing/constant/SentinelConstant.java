package com.luyouxiao.mianshixing.constant;

/**
 * @author 鹿又笑
 * @create 2024/10/7-8:33
 * @description
 */
public interface SentinelConstant {

    /**
     * 分页获取题库列表接口限流
     */
    String listQuestionBankVOByPage = "listQuestionBankVOByPage";
    /**
     * 分页获取题目列表接口限流
     */
    String listQuestionVOByPage = "listQuestionVOByPage";

    /**
     * 分页获取题目列表接口限流
     */
    String listQuestionVOByPageSentinel = "listQuestionVOByPageSentinel";

}
