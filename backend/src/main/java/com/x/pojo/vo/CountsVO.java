package com.x.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountsVO implements Serializable {
    private List<Long> userGrowthData;
    private List<Long> postGrowthData;
    private Long userTotal;
    private Long userTodayAdd;
    private Long groupTotal;
    private Long postTotal;
}
