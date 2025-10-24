package com.x.service.impl;

import com.x.common.constant.RedisKeyConstants;
import com.x.context.BaseContext;
import com.x.exception.BaseException;
import com.x.exception.LoginException;
import com.x.mapper.FriendMapper;
import com.x.mapper.NotificationMapper;
import com.x.mapper.TagMapper;
import com.x.mapper.UserMapper;
import com.x.pojo.dto.UserLoginDTO;
import com.x.pojo.dto.UserRegisterDTO;
import com.x.pojo.dto.UserUpdateDTO;
import com.x.pojo.entity.Friend;
import com.x.pojo.entity.Notification;
import com.x.pojo.entity.User;
import com.x.pojo.entity.UserInfo;
import com.x.pojo.vo.UserRecommendVO;
import com.x.pojo.vo.UserSearchVO;
import com.x.properties.GithubProperties;
import com.x.service.UserService;
import com.x.utils.GithubLogin;
import com.x.utils.ValidatorUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * UserServiceImpl 类
 * 作者：朝永
 * 日期：2025/10/22
 * 描述：
 */

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private FriendMapper   friendMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private GithubProperties githubProperties;
    @Override
    public UserInfo login(UserLoginDTO userLoginDTO)  {
        User user;
        //先判断是账号登录还是邮箱登录
        if(userLoginDTO.getAccount().isEmpty()){
            //邮箱登录
            //判断邮箱格式
            if(!ValidatorUtil.isValidEmail(userLoginDTO.getEmail())){
                throw new LoginException("邮箱格式错误!");
            }
            user=userMapper.getByEmail(userLoginDTO.getEmail());
        }
        else{
            //账号登录
            //判断账号格式
            if(userLoginDTO.getAccount().length()!=6){
                throw new LoginException("账号格式错误!");
            }
            user=userMapper.getByAccount(userLoginDTO.getAccount());
        }
        if (user==null){
            throw new LoginException("不存在该用户哦,请重试叭");
        }
        //比对密码
        String psd=user.getPassword();
        if(!psd.equals(userLoginDTO.getPassword())) {
            throw new LoginException("密码输错啦!");
        }
        //status
        if (user.getStatus()==0){
            throw new LoginException("已被封禁,请联系管理员QQ:1846326845");
        }
        return new UserInfo(user.getId(),user.getAdmin());
    }
    @Override
    public void register(UserRegisterDTO userRegisterDto) {

        if(userRegisterDto.getEmail().isEmpty() || userRegisterDto.getPassword().isEmpty() || userRegisterDto.getUsername().isEmpty()){
            throw new BaseException("信息不完整!");
        }
            //判断邮箱格式
            if(!ValidatorUtil.isValidEmail(userRegisterDto.getEmail())){
                throw new BaseException("邮箱格式错误!");
            }

        RLock lock=redissonClient.getLock(RedisKeyConstants.LOCK_REGISTER);
        lock.lock(10, TimeUnit.SECONDS);

        try {
            //判断邮箱是否已经注册
            if(userMapper.getByEmail(userRegisterDto.getEmail())!=null){
                throw new BaseException("该邮箱已经注册过了哦!");
            }
            //设置账号
            Long count=userMapper.getUserTotal();
            String account=String.format("%06d", count+1);
            userRegisterDto.setAccount(account);
            userMapper.register(userRegisterDto);
        }catch (BaseException e) {
            throw e; // 保留业务异常
        } catch (Exception e){
            throw new BaseException("未知错误!");
        }finally {
            if(lock.isHeldByCurrentThread() && lock.isLocked()){
                lock.unlock();
            }
        }
        }
    @Override
    public User getById(Long id) {
        return userMapper.getById(id);
    }


    @Override
    public void update(UserUpdateDTO userUpdateDTO) {
        //判断原密码是否正确
        User user=userMapper.getById(BaseContext.getCurUserInfo().getId());
        User user1=new User();
        if(!Objects.equals(userUpdateDTO.getNewPassword(), "")){
            if(Objects.equals(userUpdateDTO.getOldPassword(), "")) {
                throw new BaseException("请输入原密码");
            } else if(!user.getPassword().equals(userUpdateDTO.getOldPassword())) {
                throw new BaseException("原密码错误");
            } else{
                user1.setPassword(userUpdateDTO.getNewPassword());
            }
        }
        BeanUtils.copyProperties(userUpdateDTO,user1);
        user1.setId(BaseContext.getCurUserInfo().getId());
        userMapper.update(user1);
    }

    @Override
    public UserSearchVO searchByAccount(String account) {
        User user=userMapper.getByAccount(account);
        //未找到,failed
        if(user==null) {
            UserSearchVO userSearchVO = new UserSearchVO();
            userSearchVO.setStatus("failed");
            return userSearchVO;
        }
        UserSearchVO userSearchVO=new UserSearchVO();
        BeanUtils.copyProperties(user,userSearchVO);
        //找到,但是已经申请过了
        //先根据对方账号得到对方的id
        Long receiverId=userMapper.getByAccount(account).getId();
        Integer type=1;
        Notification notification= notificationMapper.getByIds(BaseContext.getCurUserInfo().getId(),receiverId,type);
        if(notification!=null) {
            userSearchVO.setStatus("applied");
            return userSearchVO;
        }
        //找到，但是是本人
        if(BaseContext.getCurUserInfo().equals(receiverId)) {
            userSearchVO.setStatus("self");
            return userSearchVO;
        }
        //找到,但是是好友了
        Friend friend= friendMapper.getByIds(BaseContext.getCurUserInfo().getId(),receiverId);
        if(friend!=null) {
            userSearchVO.setStatus("friend");
            return userSearchVO;
        }
        //找到,但是没有申请
        userSearchVO.setStatus("pending");
        return userSearchVO;
    }
    @Override
    public List<UserRecommendVO> getRecommendUsers(Long id) {
        //先得到有相同标签的用户id
        List<Long> userIds = tagMapper.getUserIds(id);
        //再得到朋友的id
        List<Long> friendIds = friendMapper.getFriendIds(id);
        friendIds.add(id);
        //将朋友的id和自己的id在userIds中进行去重
        userIds.removeAll(friendIds);
        if(userIds.isEmpty()) {
            return null;
        }
        List<User> users = userMapper.getByIds(userIds);
        List<UserRecommendVO> userRecommendVOS = new ArrayList<>();
        for(User user:users){
            userRecommendVOS.add(UserRecommendVO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .avatar(user.getAvatar())
                            .signature(user.getSignature())
                    .tags(tagMapper.getTagById(user.getId()))
                    .build());
        }
        return userRecommendVOS;
    }
    @Override
    public Long getUserCount() {
        return userMapper.getUserCount();
    }

    @Override
    public Long githubLogin(Map<String, String> body) {
        String code = body.get("code");
        if(code==null){
            throw new BaseException("code为空");
        }
        Map<String,Object> userInfo = GithubLogin.
                githubLogin(code,githubProperties.getClientId(),githubProperties.getClientSecret());
        if (userInfo == null ) {
            throw new BaseException("Failed to get GitHub user info");
        }

        UserRegisterDTO userRegisterDTO = UserRegisterDTO.builder()
                .username((String) userInfo.get("login"))
                .avatar((String) userInfo.get("avatar_url"))
                .email((String) userInfo.get("email"))
                .password("123456")
                .build();
        String account=userInfo.get("id").toString();
        //在数据库中查询是否已经登录过，即注册过了
        User user=userMapper.getByAccount(account);
        if(user!=null) {
            return user.getId();
        }
            userRegisterDTO.setAccount(account);
            userMapper.registerByGithub(userRegisterDTO);
        return userRegisterDTO.getId();
    }
    }

