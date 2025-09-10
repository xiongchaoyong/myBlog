import { defineStore } from "pinia";
import { onMounted, ref } from "vue";
import { computed } from "vue";
import { useUserStore } from "./userStore";
import GroupChat from "@/api/GroupChat";

export const useGroupChatStore = defineStore('groupChat', () => {
  //store
  const loginStore = useUserStore()
  //状态
  const myCreatedGroups = ref(JSON.parse(localStorage.getItem('myCreatedGroups') || null))
  const myJoinedGroups = ref(JSON.parse(localStorage.getItem('myJoinedGroups') || null))
  const groupMembers = ref(JSON.parse(localStorage.getItem('groupMembers') || null))
  //方法
  //get
  const getMyCreatedGroups = computed(() => {
    return myCreatedGroups.value
  })
  const getMyJoinedGroups = computed(() => {
    return myJoinedGroups.value
  })
  const getGroupMembers = computed(() => {
    return groupMembers.value
  })

  //set
  const setMyCreatedGroups = (data) => {
    myCreatedGroups.value = data
    localStorage.setItem('myCreatedGroups', JSON.stringify(data))
  }
  const setMyJoinedGroups = (data) => {
    myJoinedGroups.value = data
    localStorage.setItem('myJoinedGroups', JSON.stringify(data))
  }
  const setGroupMembers = (data) => {
    groupMembers.value = data
    localStorage.setItem('groupMembers', JSON.stringify(data))
  }

  //初始化
  const fetchMyCreatedGroups = async () => {
    const res = await GroupChat.getMyCreatedGroups(loginStore.user.id)
    if (res.data.code === 1) {
      //添加memberCount字段(群聊表中没有设置改字段)
      for (let i = 0; i < res.data.data.length; i++) {
        const ress = await GroupChat.getGroupMembersCount(res.data.data[i].id)
        res.data.data[i] = {
          ...res.data.data[i],
          memberCount: ress.data.data
        }
      }
      setMyCreatedGroups(res.data.data)
    }
  }
  const fetchMyJoinedGroups = async () => {
    const res = await GroupChat.getMyJoinedGroups(loginStore.user.id)
    if (res.data.code === 1) {
      //添加memberCount字段(群聊表中没有设置改字段)
      for (let i = 0; i < res.data.data.length; i++) {
        const ress = await GroupChat.getGroupMembersCount(res.data.data[i].id)
        res.data.data[i] = {
          ...res.data.data[i],
          memberCount: ress.data.data
        }
      }
      setMyJoinedGroups(res.data.data)
    }
  }
  const fetchGroupMembers = async (groupId) => {
    const res = await GroupChat.getGroupMembers(groupId)
    setGroupMembers(res.data.data)
  }

  // onMounted(() => {
  //   if (loginStore.isLogin) {
  //     fetchMyCreatedGroups()
  //     fetchMyJoinedGroups()
  //   }
  // })

  const makeNullAndRemove = () => {
    myCreatedGroups.value = null
    localStorage.removeItem('myCreatedGroups')
    myJoinedGroups.value = null
    localStorage.removeItem('myJoinedGroups')
    groupMembers.value = null
    localStorage.removeItem('groupMembers')
  }
  const groupChatInit = () => {
    fetchMyCreatedGroups()
    fetchMyJoinedGroups()
  }

  return {
    getMyCreatedGroups,
    setMyCreatedGroups,
    fetchMyCreatedGroups,
    fetchMyJoinedGroups,
    makeNullAndRemove,
    getMyJoinedGroups,
    fetchGroupMembers,
    getGroupMembers,
    groupChatInit
  }
})