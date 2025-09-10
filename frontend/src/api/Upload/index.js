import request from "@/utils/request";

function deleteUpload(url) {
  return request({
    method: "delete",
    url: `/user/deleteUpload/${url}`,
  });
}
export default { deleteUpload }