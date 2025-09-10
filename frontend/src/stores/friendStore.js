import { defineStore } from 'pinia';
import { computed, onMounted, ref } from 'vue';
import { useUserStore } from './userStore';
import Friend from '@/api/Friend';

export const useFriendStore = defineStore('friend', () => {

  //store
  const loginStore = useUserStore()

  //状态
  const friendList = ref(JSON.parse(localStorage.getItem('friendList') || null))

  //get  获取器（使用 computed 保持响应式）
  const getFriendList = computed(() => friendList.value);

  //set
  const setFriendList = (newList) => {
    friendList.value = newList;
    localStorage.setItem('friendList', JSON.stringify(newList));
  };
  const fetchFriendList = async () => {
    const res = await Friend.getFriendList(loginStore.user?.id)
    setFriendList(res.data.data)
  }
 

  const makeNull = () => {
    friendList.value = null
    localStorage.removeItem('friendList')
  }

  return {
    getFriendList,
    setFriendList,
    fetchFriendList,
    makeNull
  }
});