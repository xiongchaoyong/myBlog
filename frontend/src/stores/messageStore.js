import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import User from '@/api/User';
import PrivateMessage from '@/api/PrivateMessage';
import { useUserStore } from './userStore';

export const useMessageStore = defineStore('message', () => {
  //store
  const loginStore = useUserStore();
  //状态
  const mesListAll = ref(JSON.parse(localStorage.getItem('mesListAll')) || null);//当前用户的所有消息
  const mesList = ref(JSON.parse(localStorage.getItem('mesList')) || null);  //当前用户的所有消息列表
  const curMesList = ref(JSON.parse(localStorage.getItem('curMesList')) || null); //当前聊天对象的消息列表
  const curChatUser = ref(null); //当前聊天对象
  const curMesListIndex = ref(null);
  //方法

  //get
  const getMesListAll = computed(() => mesListAll.value);
  const getMesList = computed(() => mesList.value);
  const getCurChatUser = computed(() => curChatUser.value);
  const getCurMesListIndex = computed(() => curMesListIndex.value);


  //根据curChatUser获得curMesList
  const getCurMesList = computed(() => {
    //遍历mesListAll，找到当前聊天对象的所有消息
    if (curChatUser.value && mesListAll.value) {
      // 从localStorage获取当前用户ID
      const userStr = localStorage.getItem('user');
      let currentUserId = null;
      if (userStr) {
        try {
          const user = JSON.parse(userStr);
          currentUserId = user.id;
        } catch (e) {
          console.error('解析用户信息失败:', e);
        }
      }

      const filteredMessages = mesListAll.value.filter(mes =>
        (mes.senderId === curChatUser.value.id && mes.receiverId === currentUserId) ||
        (mes.receiverId === curChatUser.value.id && mes.senderId === currentUserId)
      );
      curMesList.value = filteredMessages;
      return filteredMessages;
    }
    return curMesList.value || [];
  });


  //set

  //调用aip获得
  const setMesListAll = (newMesListAll) => {
    mesListAll.value = newMesListAll;
    localStorage.setItem('mesListAll', JSON.stringify(newMesListAll));
  };

  //调用aip获得
  const setMesList = (newMesList) => {
    mesList.value = newMesList;
    localStorage.setItem('mesList', JSON.stringify(newMesList));
  };

 const loadingCurMessage = ref(false);
const getLoadingCurMessage = computed(() => loadingCurMessage.value);
  //点击获得
  const setCurChatUserAndIndex = (id, index) => {
    loadingCurMessage.value = true;
    //设置isRead为true
    mesList.value[index].isRead = true;
    //设置curMesListIndex
    curMesListIndex.value = index;
    //调用user接口，获得user对象
    User.getById(id).then(res => {
      curChatUser.value = res.data.data;
      loadingCurMessage.value = false;
    }).catch(err => {
      loadingCurMessage.value = false;
      console.error('Error fetching user:', err);
    });
  };

  const addMesListAll = (newMes) => {
    mesListAll.value.push(newMes);
    localStorage.setItem('mesListAll', JSON.stringify(mesListAll.value));
  };
  //发送信息后实时更新当前聊天对象的会话窗口，但是接收端的聊天对象的会话窗口不会更新
  const updateMesList = (newMessage) => {
    mesList.value[curMesListIndex.value].content = newMessage.content;
    mesList.value[curMesListIndex.value].createdAt = newMessage.createdAt;
    mesList.value[curMesListIndex.value].senderId = newMessage.senderId;
    mesList.value[curMesListIndex.value].receiverId = newMessage.receiverId;
  };

  //实现接收端的会话窗口的实时更新，不管是在当前聊天对象会话窗口中，还是在其他聊天对象会话窗口中
  const updateMesListByIdS = (newMsg) => {
    console.log("newMsg", newMsg);
    //根据senderId和receiverId找到匹配的会话框更新mesList
    for (let i = 0; i < mesList.value.length; i++) {
      console.log(newMsg);
      if (
        (mesList.value[i].senderId === newMsg.senderId || mesList.value[i].senderId === newMsg.receiverId) &&
        (mesList.value[i].receiverId === newMsg.receiverId || mesList.value[i].receiverId === newMsg.senderId)
      ) {
        console.log('找到匹配的会话框:', mesList.value[i])
        mesList.value[i].content = newMsg.content;
        mesList.value[i].createdAt = newMsg.createdAt;
        mesList.value[i].senderId = newMsg.senderId;
        mesList.value[i].receiverId = newMsg.receiverId;
        if (curMesListIndex.value !== i)
          mesList.value[i].isRead = false;
      }
    }
  }

  const jumpFromContacts = (curId, friendId) => {
    for (let i = 0; i < mesList.value.length; i++) {
      if ((mesList.value[i].senderId === curId || mesList.value[i].senderId === friendId) &&
        (mesList.value[i].receiverId === curId || mesList.value[i].receiverId === friendId)) {
        setCurChatUserAndIndex(friendId, i)
      }
    }
  }
  const makeNull = () => {
    curChatUser.value = null;
    curMesListIndex.value = null;
    mesList.value = null;
    curMesList.value = null;
    mesListAll.value = null;
    localStorage.removeItem('mesListAll');
    localStorage.removeItem('mesList');
    localStorage.removeItem('curMesList');
  }

  //初始化
  // 用户信息映射,这里其实直接数据库连表查询，但是为时已晚
  const userInfoMap = ref({});
  /**
   * 获取用户信息
   */
  const getUserInfo = async (userId) => {
    if (userInfoMap.value[userId]) {
      return userInfoMap.value[userId];
    }
    try {
      const res = await User.getById(userId);

      // 根据API返回的数据结构访问用户信息
      const userData = res.data.data;
      // if (!userData) {
      //   // 用户不存在，返回兜底信息
      //   return { username: '未知用户', avatar: '../assets/img/avatar.png' };
      // }
      const userInfo = {
        username: userData.username,
        avatar: userData.avatar
      };

      userInfoMap.value[userId] = userInfo;
      return userInfo;
    } catch (error) {
      console.error('获取用户信息失败:', error);
      return { username: '未知用户', avatar: '../assets/img/avatar.png' };
    }
  };

  /**
   * 为消息列表添加用户信息
   */
  const addUserInfoToMessages = async (messages) => {
    if (!messages || !Array.isArray(messages)) return messages;

    const processedMessages = [];

    for (const message of messages) {
      // 获取消息中非当前用户的ID
      const otherUserId = message.senderId === loginStore.user.id
        ? message.receiverId
        : message.senderId;

      // 获取用户信息
      const userInfo = await getUserInfo(otherUserId);

      // 创建新的消息对象，包含用户信息
      const messageWithUserInfo = {
        ...message,
        username: userInfo.username,
        avatar: userInfo.avatar
      };

      processedMessages.push(messageWithUserInfo);
    }

    return processedMessages;
  };
  const addUserInfoToMessagesIsRead = async (messages) => {
    if (!messages || !Array.isArray(messages)) return messages;

    const processedMessages = [];

    for (const message of messages) {
      // 获取消息中非当前用户的ID
      const otherUserId = message.senderId === loginStore.user.id
        ? message.receiverId
        : message.senderId;

      // 获取用户信息
      const userInfo = await getUserInfo(otherUserId);

      // 创建新的消息对象，包含用户信息
      const messageWithUserInfo = {
        ...message,
        username: userInfo.username,
        avatar: userInfo.avatar,
        isRead: true
      };

      processedMessages.push(messageWithUserInfo);
    }

    return processedMessages;
  };
  const fetchMesListAll = async () => {
    const res = await PrivateMessage.getMesListAll(loginStore.user.id);
    if (res.data.code === 1) {
      // 为消息添加用户信息
      const messagesWithUserInfo = await addUserInfoToMessages(res.data.data);
      setMesListAll(messagesWithUserInfo);
    }
  }
  const fetchMesList = async () => {
    const res = await PrivateMessage.getMesList(loginStore.user.id);
    console.log('fetchMesList API响应:', res);

    if (res.data.code === 1) {
      // 为消息添加用户信息
      const messagesWithUserInfo = await addUserInfoToMessages(res.data.data);
      const messagesWithUserInfoIsRead = await addUserInfoToMessagesIsRead(messagesWithUserInfo);
      setMesList(messagesWithUserInfoIsRead);
    }
  }

  return {
    mesListAll,
    mesList,
    curMesList,
    curChatUser,
    setMesListAll,
    setMesList,
    setCurChatUserAndIndex,
    getMesListAll,
    getMesList,
    getCurMesList,
    getCurChatUser,
    addMesListAll,
    updateMesList,
    makeNull,
    updateMesListByIdS,
    getCurMesListIndex,
    fetchMesListAll,
    fetchMesList,
    jumpFromContacts,
    getLoadingCurMessage
  };

});
