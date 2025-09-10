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

public interface UserService {
    UserInfo login(UserLoginDTO userLoginDTO) throws LoginException;

    void register(UserRegisterDTO userRegisterDto);

    User getById(Long id);

    void update(UserUpdateDTO userUpdateDTO);

    UserSearchVO searchByAccount(String account);

    void adminLogin(UserLoginDTO userLoginDTO);

    List<User> getAllUsers();

    void blockUser(Long id);

    void recoverUser(Long id);

    List<UserRecommendVO> getRecommendUsers(Long id);

    void setAdmin(Long id);

    Long getUserCount();
}
