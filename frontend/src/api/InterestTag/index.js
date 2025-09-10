import request from "@/utils/request";

function getAllTags() {
  return request({
    url: '/user/tag/get',
    method: 'get'
  })
}

function getTagsById(id) {
  return request({
    url: `/user/tag/get/${id}`,
    method: 'get'
  })
}

function updateUserTag(data) {
  return request({
    url: '/user/tag/update',
    method: 'post',
    data
  })
}

export default {
  getAllTags,
  getTagsById,
  updateUserTag
}