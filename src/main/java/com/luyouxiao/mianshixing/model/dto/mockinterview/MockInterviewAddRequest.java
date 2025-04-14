package com.luyouxiao.mianshixing.model.dto.mockinterview;

import lombok.Data;

import java.io.Serializable;

/**
 * 模拟面试添加请求
 */
@Data
public class MockInterviewAddRequest implements Serializable {

    /**
     * 工作年限
     */
    private String workExperience;

    /**
     * 工作岗位
     */
    private String jobPosition;

    /**
     * 面试难度
     */
    private String difficulty;

    private static final long serialVersionUID = 1L;
}