package com.x.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  ScrollPageVO<T> implements Serializable {
    private  Long max;
    private  Integer offset;
    private List<T> objects;
}
