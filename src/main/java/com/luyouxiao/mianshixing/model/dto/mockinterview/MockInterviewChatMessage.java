package com.luyouxiao.mianshixing.model.dto.mockinterview;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 鹿又笑
 * @create 2025/4/10-0:04
 * @description
 */
@Data
public class MockInterviewChatMessage implements Serializable {

    /**
     * 角色
     */
    private String role;

    /**
     * 消息内容
     */
    private String message;

}
