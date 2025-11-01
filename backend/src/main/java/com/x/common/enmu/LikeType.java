package com.x.common.enmu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter

public enum LikeType {
    @JsonValue
    ARTICLE((byte) 1),
    POST((byte)2),
    LEAVE_MESSAGE((byte)3),
    ARTICLE_COMMENT((byte)4);

    private final byte code;

    LikeType(byte code) {
        this.code = code;
    }
    @JsonValue
    public byte getCode() {
        return code;
    }

    public static LikeType fromCode(byte code) {
        for (LikeType s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知状态: " + code);
    }
    // 添加从字符串反序列化的方法
    @JsonCreator
    public static LikeType fromValue(String value) {
        try {
            // 先尝试按名称解析
            return LikeType.valueOf(value);
        } catch (IllegalArgumentException e) {
            // 如果按名称解析失败，尝试按 code 解析

            throw new IllegalArgumentException("未知的 LikeType: " + value);
        }
    }
}
