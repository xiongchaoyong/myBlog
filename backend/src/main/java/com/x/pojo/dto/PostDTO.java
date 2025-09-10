package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long id;//动态id
    private Long userId; //作者id
    private String content; //内容
    private List<String> imageUrls; //图片
    private String createdAt; //创建时间
    private UserSimpleDTO author; //作者信息
    private List<PostCommentDTO> comments; //评论相关
    private List<PostLikeDTO> likes;  //点赞相关
}


