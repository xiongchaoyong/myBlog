package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public
class PostCommentDTO {
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private String createdAt;
    private UserSimpleDTO user;
}
