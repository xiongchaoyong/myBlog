package com.x.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupSessionVO implements Serializable {
    private Long groupId;         // 群ID
    private String groupName;     // 群名称
    private String groupAvatar;   // 群头像URL
    private Long messageId;       // 最近一条消息ID
    private String content;       // 最近一条消息内容
    private Date messageTime;   // 最近一条消息时间
    private Long senderId;        // 最近一条消息发送者ID
    private String senderName;    // 最近一条消息发送者昵称
    private String senderAvatar;  // 最近一条消息发送者头像URL
}