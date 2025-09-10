import request from "@/utils/request";

 export interface ChatMessage {
  id: number
  sessionId: number
  userId: number 
  role: 'user' | 'ai'
  content: string
  isWaiting: boolean 
}



function getCurMsgList(sessionId: number){
  return request({
    url: `/ai/getCurMsgList/${sessionId}`,
    method: 'GET',
  })
}

function addMsg(msg: ChatMessage){
  return request({
    url: `/ai/addMsg`,
    method: 'POST',
    data: msg
  })
}

function deleteMsg(msgId: number){
  return request({
    url: `/ai/deleteMsg/${msgId}`,
    method: 'DELETE',
  })
}

export default {
  getCurMsgList,
  addMsg,
  deleteMsg
}