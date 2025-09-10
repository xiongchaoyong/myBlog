package com.x.service;

import com.x.pojo.entity.Tool;

import java.util.List;

public interface ToolService {
    List<Tool> getAll();

    Long add(Tool tool);
}
