import request from "@/utils/request";

export interface Session {
  id: number
  userId: number
  title: string
  createdTime: number[]
  isLoading: boolean
  isWaiting: boolean
}

export interface UpdateSessionTitleDTO {
  sessionId: number
  title: string
}
function getSessionList(){
  return request({
    url: `/ai/getSessionList`,
    method: 'GET',
  })
}

function addSession(session:Session){
  return request({
    url: `/ai/addSession`,
    method: 'POST',
    data: session
  })
}

function updateSessionTitle(dto:UpdateSessionTitleDTO){
  return request({
    url: `/ai/updateSessionTitle`,
    method: 'POST',
    data: dto
  })
}

function deleteSession(sessionId: number){
  return request({
    url: `/ai/deleteSession/${sessionId}`,
    method: 'DELETE',
  })
}
export default {
  getSessionList,
  addSession,
  updateSessionTitle,
  deleteSession
}