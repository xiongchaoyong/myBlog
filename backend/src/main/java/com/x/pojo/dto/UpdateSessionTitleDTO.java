package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateSessionTitleDTO implements Serializable {
    private Long sessionId;
    private String title;
}
