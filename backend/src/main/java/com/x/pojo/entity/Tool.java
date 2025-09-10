package com.x.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tool implements Serializable {
    private Long id;
    private String name;
    private String url;
    private String description;
    private String avatar;
    private String cardColor;
}
