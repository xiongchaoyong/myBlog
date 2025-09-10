import {defineStore}   from 'pinia'
import type { Tool } from '@/api/tool'
import {computed, ref, watch} from 'vue'
export const useToolStore =defineStore('tool',()=>{
  //state
  const allTools=ref<Tool[]>(JSON.parse(localStorage.getItem('tools') || '[]'))

  //get
  const getTools=computed(()=>{
    return allTools.value
  })

watch(() => allTools.value, (newVal) => {
  localStorage.setItem('tools', JSON.stringify(newVal))
}, { deep: true })

  //set
  const setTools=(value:Tool[])=>{
    allTools.value=value
  }

  return{
    getTools,
    setTools
  }
})
