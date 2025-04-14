package com.luyouxiao.mianshixing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luyouxiao.mianshixing.common.ErrorCode;
import com.luyouxiao.mianshixing.constant.CommonConstant;
import com.luyouxiao.mianshixing.exception.BusinessException;
import com.luyouxiao.mianshixing.exception.ThrowUtils;
import com.luyouxiao.mianshixing.manager.AiManager;
import com.luyouxiao.mianshixing.mapper.MockInterviewMapper;
import com.luyouxiao.mianshixing.model.dto.mockinterview.MockInterviewAddRequest;
import com.luyouxiao.mianshixing.model.dto.mockinterview.MockInterviewChatMessage;
import com.luyouxiao.mianshixing.model.dto.mockinterview.MockInterviewEventRequest;
import com.luyouxiao.mianshixing.model.dto.mockinterview.MockInterviewQueryRequest;
import com.luyouxiao.mianshixing.model.dto.question.QuestionQueryRequest;
import com.luyouxiao.mianshixing.model.entity.MockInterview;
import com.luyouxiao.mianshixing.model.entity.Question;
import com.luyouxiao.mianshixing.model.entity.User;
import com.luyouxiao.mianshixing.model.enums.MockInterviewEnum;
import com.luyouxiao.mianshixing.model.enums.MockInterviewStatusEnum;
import com.luyouxiao.mianshixing.service.MockInterviewService;
import com.luyouxiao.mianshixing.utils.SqlUtils;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 鹿又笑
 * @create 2025/4/9-21:00
 * @description
 */
@Service
@Slf4j
public class MockInterviewServiceImpl extends ServiceImpl<MockInterviewMapper, MockInterview> implements MockInterviewService {

    @Resource
    private AiManager aiManager;

    /**
     * 创建模拟面试
     * @param mockInterviewAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public Long createMockInterview(MockInterviewAddRequest mockInterviewAddRequest, User loginUser) {
        // 1. 参数校验
        if (mockInterviewAddRequest == null || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String workExperience = mockInterviewAddRequest.getWorkExperience();
        String jobPosition = mockInterviewAddRequest.getJobPosition();
        String difficulty = mockInterviewAddRequest.getDifficulty();
        ThrowUtils.throwIf(StrUtil.hasBlank(workExperience, jobPosition, difficulty), ErrorCode.PARAMS_ERROR, "参数错误");
        // 2. 封装插入到数据库中的对象
        MockInterview mockInterview = new MockInterview();
        mockInterview.setWorkExperience(workExperience);
        mockInterview.setJobPosition(jobPosition);
        mockInterview.setDifficulty(difficulty);
        mockInterview.setUserId(loginUser.getId());
        mockInterview.setStatus(MockInterviewStatusEnum.TO_START.getValue());
        // 插入数据库
        boolean save = this.save(mockInterview);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "创建失败");
        return mockInterview.getId();
    }

    /**
     * 构造查询条件
     * @param mockInterviewQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<MockInterview> getQueryWrapper(MockInterviewQueryRequest mockInterviewQueryRequest) {
        QueryWrapper<MockInterview> queryWrapper = new QueryWrapper<>();
        if (mockInterviewQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = mockInterviewQueryRequest.getId();
        String workExperience = mockInterviewQueryRequest.getWorkExperience();
        String jobPosition = mockInterviewQueryRequest.getJobPosition();
        String difficulty = mockInterviewQueryRequest.getDifficulty();
        Integer status = mockInterviewQueryRequest.getStatus();
        Long userId = mockInterviewQueryRequest.getUserId();
        String sortField = mockInterviewQueryRequest.getSortField();
        String sortOrder = mockInterviewQueryRequest.getSortOrder();

        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.like(StringUtils.isNotBlank(workExperience), "workExperience", workExperience);
        queryWrapper.like(StringUtils.isNotBlank(jobPosition), "jobPosition", jobPosition);
        queryWrapper.like(StringUtils.isNotBlank(difficulty), "difficulty", difficulty);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    /**
     * 处理模拟面试事件
     * @param mockInterviewEventRequest
     * @param loginUser
     * @return
     */
    @Override
    public String handleMockInterviewEvent(MockInterviewEventRequest mockInterviewEventRequest, User loginUser) {
        Long id = mockInterviewEventRequest.getId();
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错错误");
        }
        MockInterview mockInterview = this.getById(id);
        ThrowUtils.throwIf(mockInterview == null, ErrorCode.NOT_FOUND_ERROR, "模拟面试未创建");
        // 如果不是本人创建的面试 报错
        if (!loginUser.getId().equals(mockInterview.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有权限");
        }
        String event = mockInterviewEventRequest.getEvent();
        MockInterviewEnum eventEnum = MockInterviewEnum.getEnumByValue(event);
        switch (eventEnum) {
            // -- 处理开始事件
            // 用户进入模拟面试，发送“开始”事件，修改模拟面试的状态为“已开始”，AI 要给出对应的回复
            case START:
                return handleChatStartEvent(mockInterview);
            // -- 处理对话事件
            // 用户可以和 AI 面试官发送消息，发送“消息”事件，携带上要发送的消息内容，AI 要给出对应的回复
            case CHAT:
                return handleChatMessageEvent(mockInterviewEventRequest, mockInterview);
            case END:
                // -- 处理结束事件
                // 退出模拟面试，发送“退出”事件，AI 给出面试的复盘总结，修改状态为“已结束”
                return handleChatEndEvent(mockInterview);
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
    }

    /**
     * 处理 AI 对话结束事件
     * @param mockInterview
     * @return
     */
    private String handleChatEndEvent(MockInterview mockInterview) {
        // 构造消息列表（先获取之前的消息记录）
        String historyMessage = mockInterview.getMessages();
        List<MockInterviewChatMessage> historyMessageList = JSONUtil.parseArray(historyMessage).toList(MockInterviewChatMessage.class);
        List<ChatMessage> chatMessages = transformToChatMessage(historyMessageList);
        // 构造用户结束消息
        String endUserPrompt = "结束";
        ChatMessage endChatMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(endUserPrompt).build();
        chatMessages.add(endChatMessage);
        // 调用 AI
        String aiResponse = aiManager.doChat(chatMessages);
        ChatMessage endAssistantMessage = ChatMessage.builder().role(ChatMessageRole.ASSISTANT).content(aiResponse).build();
        chatMessages.add(endAssistantMessage);
        // 保存消息，更新状态
        List<MockInterviewChatMessage> chatMessageList = transformFromChatMessage(chatMessages);
        String jsonStr = JSONUtil.toJsonStr(chatMessageList);
        // 更新数据库
        MockInterview updateMockInterview = new MockInterview();
        updateMockInterview.setId(mockInterview.getId());
        updateMockInterview.setMessages(jsonStr);
        updateMockInterview.setStatus(MockInterviewStatusEnum.ENDED.getValue());
        boolean newResult = this.updateById(updateMockInterview);
        ThrowUtils.throwIf(!newResult, ErrorCode.OPERATION_ERROR, "更新失败");
        log.info("模拟面试结束,{}", updateMockInterview);
        return aiResponse;
    }

    /**
     * 处理 AI 对话消息事件
     * @param mockInterviewEventRequest
     * @param mockInterview
     * @return
     */
    private String handleChatMessageEvent(MockInterviewEventRequest mockInterviewEventRequest, MockInterview mockInterview) {
        String message = mockInterviewEventRequest.getMessage();
        // 构造消息列表（先获取之前的消息记录）
        String historyMessage = mockInterview.getMessages();
        List<MockInterviewChatMessage> historyMessageList = JSONUtil.parseArray(historyMessage).toList(MockInterviewChatMessage.class);
        List<ChatMessage> chatMessages = transformToChatMessage(historyMessageList);
        ChatMessage chatUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(message).build();
        chatMessages.add(chatUserMessage);
        // 调用 AI
        String aiResponse = aiManager.doChat(chatMessages);
        ChatMessage chatAssistantMessage = ChatMessage.builder().role(ChatMessageRole.ASSISTANT).content(aiResponse).build();
        chatMessages.add(chatAssistantMessage);
        // 保存消息，更新状态
        List<MockInterviewChatMessage> chatMessageList = transformFromChatMessage(chatMessages);
        String jsonStr = JSONUtil.toJsonStr(chatMessageList);
        // 更新数据库
        MockInterview updateMockInterview = new MockInterview();
        updateMockInterview.setId(mockInterview.getId());
        updateMockInterview.setMessages(jsonStr);
        // 如果 AI 主动结束面试，更改状态
        if (aiResponse.contains("【面试结束】")) {
            updateMockInterview.setStatus(MockInterviewStatusEnum.ENDED.getValue());
        }
        boolean newResult = this.updateById(updateMockInterview);
        ThrowUtils.throwIf(!newResult, ErrorCode.OPERATION_ERROR, "更新失败");
        return aiResponse;
    }

    /**
     * 处理 AI 对话开始事件
     * @param mockInterview
     * @return
     */
    private String handleChatStartEvent(MockInterview mockInterview) {
        // 构造消息列表
        // 定义 AI 的 Prompt
        String systemPrompt = String.format("你是一位严厉的程序员面试官，我是候选人，来应聘 %s 的 %s 岗位，面试难度为 %s。请你向我依次提出问题（最多 20 个问题），我也会依次回复。在这期间请完全保持真人面试官的口吻，适当引导候选人、或者表达出你对候选人回答的态度。\n" +
                "必须满足如下要求：\n" +
                "1. 当学员回复 “开始” 时，你要正式开始面试\n" +
                "2. 当学员表示希望 “结束面试” 时，你要结束面试\n" +
                "3. 此外，当你觉得这场面试可以结束时（比如候选人回答结果较差、与工作年限的招聘需求差距过大、或者候选人态度不礼貌），需要主动提出面试结束，不用继续询问更多问题了。并且要在回复中包含字符串【面试结束】并且停止提问\n" +
                "4. 面试结束后，应该给出候选人整场面试的表现和总结。", mockInterview.getWorkExperience(), mockInterview.getJobPosition(), mockInterview.getDifficulty());
        String userPrompt = "开始";
        final ArrayList<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(systemPrompt).build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(userPrompt).build();
        messages.add(systemMessage);
        messages.add(userMessage);
        // 调用 AI
        String answer = aiManager.doChat(messages);
        ChatMessage assistantMessage = ChatMessage.builder().role(ChatMessageRole.ASSISTANT).content(answer).build();
        messages.add(assistantMessage);
        // 保存记录，并更新状态
        List<MockInterviewChatMessage> chatMessageList = transformFromChatMessage(messages);
        String jsonStr = JSONUtil.toJsonStr(chatMessageList);
        // 更新数据库
        MockInterview updateMockInterview = new MockInterview();
        updateMockInterview.setStatus(MockInterviewStatusEnum.IN_PROGRESS.getValue());
        updateMockInterview.setMessages(jsonStr);
        updateMockInterview.setId(mockInterview.getId());
        boolean result = this.updateById(updateMockInterview);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");
        return answer;
    }

    /**
     * 消息对象记录转换
     * @param chatMessageList
     * @return
     */
    private List<MockInterviewChatMessage> transformFromChatMessage(List<ChatMessage> chatMessageList) {
        return chatMessageList.stream().map(chatMessage -> {
            MockInterviewChatMessage mockInterviewChatMessage = new MockInterviewChatMessage();
            mockInterviewChatMessage.setRole(chatMessage.getRole().value());
            mockInterviewChatMessage.setMessage(chatMessage.stringContent());
            return mockInterviewChatMessage;
        }).collect(Collectors.toList());
    }

    /**
     * 消息记录对象转换
     *
     * @param chatMessageList
     * @return
     */
    List<ChatMessage> transformToChatMessage(List<MockInterviewChatMessage> chatMessageList) {
        return chatMessageList.stream().map(chatMessage -> {
            ChatMessage tempChatMessage = ChatMessage.builder().role(ChatMessageRole.valueOf(StringUtils.upperCase(chatMessage.getRole())))
                    .content(chatMessage.getMessage()).build();
            return tempChatMessage;
        }).collect(Collectors.toList());
    }

}
