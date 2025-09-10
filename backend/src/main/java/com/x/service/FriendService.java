package com.x.service;

import com.x.pojo.dto.AddFriendDTO;
import com.x.pojo.dto.DeleteFriendDTO;
import com.x.pojo.vo.FriendVO;
import com.x.utils.Result;

import java.util.List;

public interface FriendService {
    Result<List<FriendVO>> getFriendList(Long id);

    void addFriend(AddFriendDTO addFriendDTO);

    void deleteFriend(DeleteFriendDTO deleteFriendDTO);

}
