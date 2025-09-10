import request from "@/utils/request";

function addLike(data) {
  return request({
    url: "/user/postLike/addLike",
    method: "post",
    data,
  });
}

function deleteLike(data) {
  return request({
    url: "/user/postLike/deleteLike",
    method: "post",
    data,
  });
}

export default { addLike, deleteLike };