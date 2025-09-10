import request from "@/utils/request";

function sendGreetMessage(data) {
  return request({
    url: "/user/groupMessage/sendGreetMessage",
    method: "post",
    data
  });
}

function getMesListAll(id) {
  return request({
    url: `/user/groupMessage/getMesListAll/${id}`, // 路径参数格式
    method: "get"
  });
}
function getMesList(id) {
  return request({
    url: `/user/groupMessage/getMesList/${id}`, // 路径参数格式
    method: "get"
  });
}
export default { sendGreetMessage, getMesListAll, getMesList };