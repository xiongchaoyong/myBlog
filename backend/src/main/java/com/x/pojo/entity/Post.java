package com.x.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post implements Serializable {
    private Long id;
    private Long userId;
    private String content;
    private List<String> imageUrls;
    private String createdAt;
}
