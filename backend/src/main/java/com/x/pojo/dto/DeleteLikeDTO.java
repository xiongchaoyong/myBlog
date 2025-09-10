package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteLikeDTO implements Serializable {
    private Long postId;
    private Long userId;
}
