package com.x.controller.user;

import com.x.pojo.dto.UserLoginDTO;
import com.x.pojo.dto.UserRegisterDTO;
import com.x.pojo.dto.UserUpdateDTO;
import com.x.pojo.entity.User;
import com.x.pojo.entity.UserInfo;
import com.x.pojo.vo.UserLoginVO;
import com.x.pojo.vo.UserRecommendVO;
import com.x.pojo.vo.UserSearchVO;
import com.x.properties.JwtProperties;
import com.x.service.UserService;
import com.x.utils.JwtUtil;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/user/user")
@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserService userService;
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) throws LoginException {
        UserInfo userInfo=userService.login(userLoginDTO);
        //生成token
        Map<String,Object> claims=new HashMap<>();
        claims.put("USER_ID",userInfo.getId());
        claims.put("USER_ROLE",userInfo.getAdmin());
        String token= JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                        claims);

        UserLoginVO userLoginVO=UserLoginVO.builder().id(userInfo.getId()).token(token).build();
        return Result.success(userLoginVO);
    }
    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterDTO userRegisterDto){
        userService.register(userRegisterDto);
        return Result.success();
    }
    @GetMapping("/getById/{id}")
    public Result<User> getById(@PathVariable  Long id){
       User user= userService.getById(id);
       return Result.success(user);
    }
    @PostMapping("/update")
    public Result update(@RequestBody UserUpdateDTO userUpdateDTO){
        userService.update(userUpdateDTO);
        return Result.success();
    }
    @GetMapping("/searchByAccount/{account}")
    public Result<UserSearchVO> searchByAccount(@PathVariable String account){
        UserSearchVO userSearchVO=userService.searchByAccount(account);
        return Result.success(userSearchVO);
    }
    @GetMapping("/getRecommendUsers/{id}")
    public  Result<List<UserRecommendVO>> getRecommendUsers(@PathVariable Long id){
        List<UserRecommendVO> userRecommendVOS=userService.getRecommendUsers(id);
        return Result.success(userRecommendVOS);
    }

    @GetMapping("/getUserCount")
    public Result<Long> getUserCount(){
        Long count=userService.getUserCount();
        return Result.success(count);
    }
}
