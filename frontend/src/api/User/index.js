import request from "@/utils/request";

function login(data) {
  return request({
    url: "/user/user/login",
    method: "post",
    data
  });
}
function register(data) {
  return request({
    url: "/user/user/register",
    method: "post",
    data
  });
}
function getById(id) {
  return request({
    url: `/user/user/getById/${id}`, // 路径参数格式
    method: "get"
    // 无需 params 字段，id 已包含在 URL 中
  });
}

function update(data) {
  return request({
    url: "/user/user/update",
    method: "post",
    data
  });
}
function searchByAccount(account) {
  return request({
    url: `/user/user/searchByAccount/${account}`,
    method: "get"
  });
}
function getRecommendUsers(id) {
  return request({
    url: `/user/user/getRecommendUsers/${id}`,
    method: "get"
  });
}

function getUserCount(){
  return request({
    url: `/user/user/getUserCount`,
    method: "get"
  })
}
export default { login, register, getById, update, searchByAccount, getRecommendUsers,getUserCount }