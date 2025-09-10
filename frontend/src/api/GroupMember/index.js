import request from "@/utils/request";

function addMember(data) {
  return request({
    url: `user/groupMember/addMember`,
    method: "post",
    data
  });
}
function deleteMember(data) {
  return request({
    url: `user/groupMember/deleteMember`,
    method: "delete",
    data
  });
}
export default { addMember, deleteMember };