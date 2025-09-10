package com.x.service.impl;

import com.x.mapper.ToolMapper;
import com.x.pojo.entity.Tool;
import com.x.service.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolServiceImpl implements ToolService {
    @Autowired
    private ToolMapper toolMapper;

    @Override
    public List<Tool> getAll() {
        return toolMapper.getAll();
    }

    @Override
    public Long add(Tool tool) {
        toolMapper.add(tool);
       return tool.getId();
    }
}
