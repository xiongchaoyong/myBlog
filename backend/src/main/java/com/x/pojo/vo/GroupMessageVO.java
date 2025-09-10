package com.x.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMessageVO implements Serializable {
    private Long messageId;       // 消息ID
    private Long groupId;         // 群ID
    private String groupName;     // 群名称
    private String groupAvatar;   // 群头像URL
    private String content;       // 消息内容
    private Date messageTime;   // 消息发送时间
    private Long senderId;        // 发送者ID
    private String senderName;    // 发送者昵称
    private String senderAvatar;  // 发送者头像URL
}