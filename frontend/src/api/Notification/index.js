import request from "@/utils/request";

function sendRequest(data) {
  return request({
    url: '/user/notification/sendRequest',
    method: 'post',
    data
  })
}
function getReceiveNotificationList(id) {
  //路径传 参
  return request({
    url: `/user/notification/getReceiveNotificationList/${id}`,
    method: 'get'
  })
}
function getSendNotificationList(id) {
  //路径传 参
  return request({
    url: `/user/notification/getSendNotificationList/${id}`,
    method: 'get'
  })
}
function updateStatus(data) {
  return request({
    url: '/user/notification/updateStatus',
    method: 'post',
    data
  })
}
function deleteNotification(data) {
  return request({
    url: '/user/notification/deleteNotification',
    method: 'post',
    data
  })
}
export default { sendRequest, getReceiveNotificationList, getSendNotificationList, updateStatus, deleteNotification };