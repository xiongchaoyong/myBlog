import request from "@/utils/request";

function getFriendList(id) {
  return request({
    url: `/user/friend/getFriendList/${id}`,
    method: "get"
  });
}
function addFriend(data) {
  return request({
    url: '/user/friend/addFriend',
    method: 'post',
    data
  })
}
function deleteFriend(data) {
  return request({
    url: '/user/friend/deleteFriend',
    method: 'post',
    data
  })
}
export default { getFriendList, addFriend, deleteFriend };