package com.x.mapper;

import com.x.pojo.dto.UserRegisterDTO;
import com.x.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user where account=#{account}")
    User getByAccount(String account);

    @Insert("insert into user (email,password,username,account)  " +
            "values (#{email},#{password},#{username},#{account})")
    void register(UserRegisterDTO userRegisterDto);
    @Select("select * from user where id=#{id}")
    User getById(Long id);
    void update(User user);

    List<Long> getUserGrowthData();

    @Select("select count(*) from user")
    Long getUserTotal();

    Long getUserTodayAdd();

    List<User> getByIds(List<Long> userIds);

    @Select("select * from user where email= #{email}")
    User getByEmail(String account);

    @Select("select count(*) from user")
    Long getUserCount();

    Long registerByGithub(UserRegisterDTO userRegisterDTO);

}
