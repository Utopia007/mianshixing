package com.luyouxiao.mianshixing.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luyouxiao.mianshixing.model.dto.mockinterview.MockInterviewAddRequest;
import com.luyouxiao.mianshixing.model.dto.mockinterview.MockInterviewEventRequest;
import com.luyouxiao.mianshixing.model.dto.mockinterview.MockInterviewQueryRequest;
import com.luyouxiao.mianshixing.model.entity.MockInterview;
import com.luyouxiao.mianshixing.model.entity.User;

/**
 * @author 鹿又笑
 * @create 2025/4/9-20:53
 * @description
 */
public interface MockInterviewService extends IService<MockInterview> {

    /**
     * 创建模拟面试
     * @param mockInterviewAddRequest
     * @param loginUser
     * @return
     */
    Long createMockInterview(MockInterviewAddRequest mockInterviewAddRequest, User loginUser);

    /**
     * 构造查询条件
     * @param mockInterviewQueryRequest
     * @return
     */
    QueryWrapper<MockInterview> getQueryWrapper(MockInterviewQueryRequest mockInterviewQueryRequest);

    /**
     * 处理模拟面试事件
     * @param mockInterviewEventRequest
     * @param loginUser
     * @return
     */
    String handleMockInterviewEvent(MockInterviewEventRequest mockInterviewEventRequest, User loginUser);



}
