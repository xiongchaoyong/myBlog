import { defineStore } from 'pinia';
import { computed, ref, watch } from 'vue';
import type { Author, Moment } from '@/api/Post'

export const usePostStore = defineStore('post', () => {
  //状态
  const momentsList = ref<Moment[]>(JSON.parse(localStorage.getItem('momentsList') || '[]'))

  //方法  
  //get
  const getMonentList = computed(() => {
    return momentsList.value;
  })

  //set
  const setMomentsList = (value: Moment[]) => {
    momentsList.value = value
  }


  const addPost = async (newMoment: Moment) => {  
    if (!Array.isArray(momentsList.value)) {
      momentsList.value = []
    }
    momentsList.value.unshift(newMoment)
    // 保持与setMomentsList一致，更新localStorage

  }

  // 在 store 里
watch(() => momentsList.value, (newVal) => {
  localStorage.setItem('momentsList', JSON.stringify(newVal))
}, { deep: true })

  //销毁
  const destroy = () => {
    localStorage.removeItem('momentsList')
    momentsList.value = []
  }
  return {
    getMonentList,
    setMomentsList,
    destroy,
    addPost
  }
});