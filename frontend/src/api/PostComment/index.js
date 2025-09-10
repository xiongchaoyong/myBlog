import request from "@/utils/request";

function addComment(data) {
  return request({
    url: "/user/postComment/addComment",
    method: "post",
    data,
  });
}

export default { addComment };