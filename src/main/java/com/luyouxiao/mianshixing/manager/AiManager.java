package com.luyouxiao.mianshixing.manager;

import cn.hutool.core.collection.CollUtil;
import com.luyouxiao.mianshixing.common.ErrorCode;
import com.luyouxiao.mianshixing.config.AiConfig;
import com.luyouxiao.mianshixing.exception.BusinessException;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChoice;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 鹿又笑
 * @create 2025/4/4-21:54
 * @description
 */
@Component
public class AiManager {

    @Resource
    private ArkService aiService;

    private final String DEFAULT_MODE = "deepseek-v3-250324";

    /**
     * 调用 AI 接口，获取想要字符串
     * @param userPrompt
     * @return
     */
    public String doChat(String userPrompt) {
        return doChat("", userPrompt, DEFAULT_MODE);
    }

    public String doChat(String systemPrompt, String userPrompt) {
        return doChat(systemPrompt, userPrompt, DEFAULT_MODE);
    }

    public String doChat(String systemPrompt, String userPrompt, String defaultMode) {
        // 构造消息列表
        ArrayList<ChatMessage> message = new ArrayList<>();
        final ChatMessage systemChatMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(systemPrompt).build();
        final ChatMessage userChatMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(userPrompt).build();
        message.add(systemChatMessage);
        message.add(userChatMessage);
        // 构造请求
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(defaultMode)
                .messages(message)
                .build();
        // 调用接口发送请求
        List<ChatCompletionChoice> choices = aiService.createChatCompletion(completionRequest).getChoices();
        if (CollUtil.isNotEmpty(choices)) {
            return choices.get(0).getMessage().stringContent();
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 调用失败，未返回结果");
    }

    /**
     * 调用 AI 接口，获取响应字符串（允许传入自定义的消息列表）
     *
     * @param messages
     * @param model
     * @return
     */
    public String doChat(List<ChatMessage> messages, String model) {
        // 构造请求
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .build();
        // 调用接口发送请求
        List<ChatCompletionChoice> choices = aiService.createChatCompletion(completionRequest).getChoices();
        if (CollUtil.isNotEmpty(choices)) {
            return choices.get(0).getMessage().stringContent();
        }
        throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 调用失败，未返回结果");
    }

    /**
     * 调用 AI 接口，获取响应字符串（允许传入自定义的消息列表，使用默认模型）
     *
     * @param messages
     * @return
     */
    public String doChat(List<ChatMessage> messages) {
        return doChat(messages, DEFAULT_MODE);
    }

}
