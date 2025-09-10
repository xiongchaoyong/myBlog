import request from "@/utils/request";

function getMesListAll(id) {
  return request({
    url: `/user/privateMessage/getMesListAll/${id}`, // 路径参数格式
    method: "get"
    // 无需 params 字段，id 已包含在 URL 中
  });
}
function getMesList(id) {
  return request({
    url: `/user/privateMessage/getMesList/${id}`, // 路径参数格式
    method: "get"
    // 无需 params 字段，id 和 otherId 已包含在 URL 中
  });
}
function sendMessage(message) {
  return request({
    url: "/user/privateMessage/sendMessage",
    method: "post",
    data: message
  });
}

function sendGreetMessage(mes) {
  return request({
    url: "/user/privateMessage/sendGreetMessage",
    method: "post",
    data: mes
  });
}

function deleteMesList(friendId) {
  return request({
    method: "delete",
    url: `/user/privateMessage/deleteMesList/${friendId}`
  })
}

export default {
  getMesListAll,
  getMesList,
  sendMessage,
  sendGreetMessage,
  deleteMesList
};
