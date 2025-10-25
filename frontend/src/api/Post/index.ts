import request from "@/utils/request";


// 用户信息
export interface Author {
  id: number;
  username: string;
  avatar: string;
}

// 点赞信息
export interface Like {
  userId: number;
  user?: Author; // 有的场景需要显示点赞用户信息
}

// 评论信息
export interface Comment {
  id: number;
  userId: number;
  content: string;
  createdAt: string;
  user: Author;
}

// 动态信息
export interface Moment {
  id: number;
  userId: number;
  content: string;
  imageUrls: string[];
  createdAt: string;
  author: Author;
  comments: Comment[];
  likes: Like[];
}

function getPosts() {
  return request({
    url: `/user/post/getPosts`,
    method: "get",
  });
}
function sendPost(data: Moment) {
  return request({
    url: "/user/post/sendPost",
    method: "post",
    data
  });
}
function deletePost(id: number) {
  return request({
    url: `/user/post/deletePost/${id}`,
    method: "delete"
  });
}

function getFivePosts() {
  return request({
    url: `/user/post/getFivePosts`,
    method: "get",
  });
}
function getMyPostsCount() {
  return request({
    url: `/user/post/getMyPostsCount`,
    method: "get",
  });
}
function getPostsCount(){
  return request({
    url: `/user/post/getPostsCount`,
    method: "get",
  });
}

function getScrollPosts(max: number, offset: number) {
  return request({
    url: `/user/post/scroll`,
    method: "get",
    params: {
      max,
      offset
    }
  });
}

export default { getPosts, sendPost, deletePost, getFivePosts, getMyPostsCount, getPostsCount ,getScrollPosts};
