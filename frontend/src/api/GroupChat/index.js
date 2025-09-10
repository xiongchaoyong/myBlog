import request from "@/utils/request";

function getMyCreatedGroups(id) {
  return request({
    url: `user/groupChat/getMyCreatedGroups/${id}`,
    method: "get"
  });
}

function getMyJoinedGroups(id) {
  return request({
    url: `user/groupChat/getMyJoinedGroups/${id}`,
    method: "get"
  });
}

function getGroupMembersCount(groupId) {
  return request({
    url: `user/groupChat/getGroupMembersCount/${groupId}`,
    method: "get"
  });
}

function getGroupMembers(groupId) {
  return request({
    url: `user/groupChat/getGroupMembers/${groupId}`,
    method: "get"
  });
}

function updateGroup(data) {
  return request({
    url: "user/groupChat/updateGroup",
    method: "post",
    data
  });
}

function createGroup(data) {
  return request({
    url: "user/groupChat/createGroup",
    method: "post",
    data
  });
}
function dissolveGroup(id) {
  return request({
    url: `user/groupChat/dissolveGroup/${id}`,
    method: "delete"
  });
}

function searchGroup(account) {
  return request({
    url: `user/groupChat/searchGroup/${account}`,
    method: "get"
  });
}
export default {
  getMyCreatedGroups,
  getGroupMembersCount,
  getMyJoinedGroups,
  getGroupMembers,
  updateGroup,
  createGroup,
  dissolveGroup,
  searchGroup
};