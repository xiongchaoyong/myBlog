package com.x.mapper;

import com.x.pojo.entity.Tool;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ToolMapper {

    @Select("select * from tool")
    List<Tool> getAll();

    @Insert("insert into tool(name,url,description,avatar,card_color) values(#{name},#{url},#{description},#{avatar},#{cardColor})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void add(Tool tool);
}
