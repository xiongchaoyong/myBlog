package com.x.service.impl;

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
import com.x.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


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
    @Override
    public UserInfo login(UserLoginDTO userLoginDTO)  {
        String account=userLoginDTO.getAccount();
        User user=new User();
        if(account.contains("@"))
            user=userMapper.getByEmail(account);
        else
         user=userMapper.getByAccount(account);
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
        //设置账号
        Long count=userMapper.getUserTotal();
        String account=String.format("%06d", count+1);
        userRegisterDto.setAccount(account);
        try {
            userMapper.register(userRegisterDto);
        }
        catch (Exception e){
            throw new BaseException("邮箱已经注册过了哦!");
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
        if(userUpdateDTO.getNewPassword()!=""){
            if(userUpdateDTO.getOldPassword()== "")
                throw new BaseException("请输入原密码");
            else if(!user.getPassword().equals(userUpdateDTO.getOldPassword()))
                throw new BaseException("原密码错误");
            else{
                user1.setPassword(userUpdateDTO.getNewPassword());
            }
        }
        BeanUtils.copyProperties(userUpdateDTO,user1);
        user1.setId(BaseContext.getCurUserInfo().getId());

        System.out.println(user1);
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
    public void adminLogin(UserLoginDTO userLoginDTO) {
        String account=userLoginDTO.getAccount();
        User user=userMapper.getByAccount(account);
        System.out.println(user);
        if (user==null){
            throw new LoginException("不存在该账号哦,请重试叭");
        }
        //比对密码
        String psd=user.getPassword();
        if(!psd.equals(userLoginDTO.getPassword())) {
            throw new LoginException("密码输错啦!");
        }
        //是否为管理员
        if(user.getAdmin()==0)
            throw new LoginException("您不是管理员哦");
        //status
        if (user.getStatus()==0){
            throw new LoginException("已被封禁,请联系管理员QQ:1846326845");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }

    @Override
    public void blockUser(Long id) {
        userMapper.blockUser(id);
    }

    @Override
    public void recoverUser(Long id) {
        userMapper.recoverUser(id);
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
        if(userIds.size()==0)
            return null;
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
    public void setAdmin(Long id) {
        userMapper.setAdmin(id);
    }

    @Override
    public Long getUserCount() {
        return userMapper.getUserCount();
    }
}
