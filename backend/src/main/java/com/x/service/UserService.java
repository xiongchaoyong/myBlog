package com.x.service;

import com.x.pojo.dto.UserLoginDTO;
import com.x.pojo.dto.UserRegisterDTO;
import com.x.pojo.dto.UserUpdateDTO;
import com.x.pojo.entity.User;
import com.x.pojo.entity.UserInfo;
import com.x.pojo.vo.UserRecommendVO;
import com.x.pojo.vo.UserSearchVO;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Map;

/**
 * UserService 类
 * 作者：朝永
 * 日期：2025/10/22
 * 描述：
 */

public interface UserService {
    UserInfo login(UserLoginDTO userLoginDTO) throws LoginException;

    void register(UserRegisterDTO userRegisterDto);

    User getById(Long id);

    void update(UserUpdateDTO userUpdateDTO);

    UserSearchVO searchByAccount(String account);

    List<UserRecommendVO> getRecommendUsers(Long id);

    Long getUserCount();

    Long githubLogin(Map<String, String>  body) ;
}
