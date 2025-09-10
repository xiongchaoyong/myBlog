import { defineStore } from 'pinia';
import { ref, computed, onMounted } from 'vue';
import { useUserStore } from './userStore';
import GroupMessage from '@/api/GroupMessage';

export const useGroupMessageStore = defineStore("groupMessage", () => {
  //store
  const loginStore = useUserStore();
  //状态
  const mesListAll = ref(JSON.parse(localStorage.getItem('groupMesListAll')) || null);//当前用户的所有消息
  const mesList = ref(JSON.parse(localStorage.getItem('groupMesList')) || null);  //当前用户的所有消息列表
  const curMesList = ref(null); //当前聊天对象的消息列表
  const curMesListIndex = ref(null)
  const curGroup = ref(null)
  //方法

  //get
  const getMesListAll = computed(() => mesListAll.value);
  const getMesList = computed(() => mesList.value);
  //根据curChatUser获得curMesList

  const getCurMesListIndex = computed(() => {
    return curMesListIndex.value;
  });
  const getCurGroup = computed(() => {
    return curGroup.value;
  });
  const getCurMesList = computed(() => {
    return mesListAll?.value?.filter(item => item.groupId === curGroup?.value?.groupId);
  });
  //set

  //调用aip获得
  const setMesListAll = (newMesListAll) => {
    mesListAll.value = newMesListAll;
    localStorage.setItem('groupMesListAll', JSON.stringify(newMesListAll));
  };
  //调用aip获得
  const setMesList = (newMesList) => {
    mesList.value = newMesList;
    localStorage.setItem('groupMesList', JSON.stringify(newMesList));
  };

  const setCurMesListIndex = (index) => {
    curMesListIndex.value = index;
    setCurGroup({
      groupId: mesList.value[index].groupId,
      groupName: mesList.value[index].groupName
    });
  }
  const setCurGroup = (data) => {
    curGroup.value = data;
  }


  const addMesListAll = (newMes) => {
    mesListAll.value.push(newMes);
    localStorage.setItem('groupMesListAll', JSON.stringify(mesListAll.value));
  };
  //发送信息后实时更新当前聊天对象的会话窗口，但是接收端的聊天对象的会话窗口不会更新
  const updateMesList = (newMessage) => {
    mesList.value[curMesListIndex.value].content = newMessage.content;
    mesList.value[curMesListIndex.value].messageTime = newMessage.messageTime;
    mesList.value[curMesListIndex.value].senderId = newMessage.senderId;
    mesList.value[curMesListIndex.value].receiverId = newMessage.receiverId;
  };

  //实现接收端的会话窗口的实时更新，不管是在当前聊天对象会话窗口中，还是在其他聊天对象会话窗口中
  const updateMesListByIdS = (newMsg) => {
    console.log('updateMesListByIdS:', curMesList.value);
    //根据senderId和receiverId找到匹配的会话框更新mesList
    for (let i = 0; i < mesList.value.length; i++) {
      if (
        // (mesList.value[i].senderId === newMsg.senderId || mesList.value[i].senderId === newMsg.groupId) &&
        // (mesList.value[i].groupId === newMsg.groupId || mesList.value[i].groupId === newMsg.senderId)
        mesList.value[i].groupId === newMsg.groupId
      ) {
        console.log('找到匹配的会话框:', mesList.value[i])
        console.log('更新会话框:', newMsg)
        mesList.value[i].content = newMsg.content;
        mesList.value[i].messageTime = newMsg.messageTime;
        mesList.value[i].senderId = newMsg.senderId;
        mesList.value[i].groupId = newMsg.groupId;
        if (curMesListIndex.value !== i)
          mesList.value[i].isRead = false;
      }
    }
  }

  // const jumpFromContacts = (curId, friendId) => {
  //   for (let i = 0; i < mesList.value.length; i++) {
  //     if ((mesList.value[i].senderId === curId || mesList.value[i].senderId === friendId) &&
  //       (mesList.value[i].receiverId === curId || mesList.value[i].receiverId === friendId)) {
  //       setCurChatUserAndIndex(friendId, i)
  //     }
  //   }
  // }
  const makeNull = () => {
    mesList.value = null;
    curMesList.value = null;
    mesListAll.value = null;
    curMesListIndex.value = null;
    curGroup.value = null;
    localStorage.removeItem('groupMesList');
    localStorage.removeItem('groupMesListAll');
  }

  const fetchMesListAll = async () => {
    const res = await GroupMessage.getMesListAll(loginStore.user.id);
    if (res.data.code === 1) {
      setMesListAll(res.data.data);
    }
    console.log('fetchMesListAll API响应:', mesListAll.value);
  }
  const fetchMesList = async () => {
    const res = await GroupMessage.getMesList(loginStore.user.id);
    if (res.data.code === 1) {
      setMesList(res.data.data);
    }
    console.log('fetchMesList API响应:', res.data.data);
  }

  // //初始化
  // onMounted(() => {
  //   fetchMesListAll();
  //   fetchMesList();
  // })
  return {
    getMesListAll,
    fetchMesList,
    fetchMesListAll,
    makeNull,
    addMesListAll,
    setMesList,
    setMesListAll,
    getCurMesList,
    getMesList,
    setCurMesListIndex,
    getCurMesListIndex,
    getCurGroup,
    updateMesList,
    updateMesListByIdS
  };
});