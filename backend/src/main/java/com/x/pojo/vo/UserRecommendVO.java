package com.x.pojo.vo;

import com.x.pojo.entity.Tag;
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
public class UserRecommendVO implements Serializable {
    private Long id;
    private String username;
    private String avatar;
    private String account;
    private List<Tag> tags;
    private String signature;
}
