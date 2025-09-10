package com.x.service.impl;

import com.x.exception.BaseException;
import com.x.mapper.FriendMapper;
import com.x.pojo.dto.AddFriendDTO;
import com.x.pojo.dto.DeleteFriendDTO;
import com.x.pojo.vo.FriendVO;
import com.x.service.FriendService;
import com.x.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {
    @Autowired
    private FriendMapper friendMapper;

    @Override
    public Result<List<FriendVO>> getFriendList(Long id) {
        return Result.success(friendMapper.getFriendList(id));
    }

    @Override
    public void addFriend(AddFriendDTO addFriendDTO) {
        try {
            friendMapper.addFriend(addFriendDTO);
        }
        catch (Exception e){
            throw new BaseException("已经是好友了哦!");
        }
    }

    @Override
    public void deleteFriend(DeleteFriendDTO deleteFriendDTO) {
        friendMapper.deleteFriend(deleteFriendDTO);
    }
}
