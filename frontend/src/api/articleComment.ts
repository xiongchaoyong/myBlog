import request from "@/utils/request";

export interface ArticleComment {
  id: number;
  parentId: number | null;
  articleId: number;
  userId: number;
  replyId: number | null;
  content: string;
  status: number;
  likeCount: number;
  replyCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface ArticleCommentDTO {
  id: number | null;
  parentId: number | null;
  articleId: number;
  userId: number;
  replyId: number | null;
  content: string;
  status: number;
  likeCount: number;
  replyCount: number;
  createdAt: string;
  updatedAt: string;
}
export interface ArticleCommentVO{
    articleComment: ArticleComment;
    username: string;
    avatar: string;
    isLike: boolean;
}

export function getTopComments(articleId: number) {
  return request({
    url: `/user/articleComment/getTopComments/${articleId}`,
    method: 'get',
  })
}

export function getKidComments(parentId: number) {
  return request({
    url: `/user/articleComment/getKidComments/${parentId}`,
    method: 'get',
  })
}

export function createComment(articleComment:ArticleCommentDTO) {
  return request({
    url: '/user/articleComment/addComment',
    method: 'post',
    data: articleComment
  })
}

export function deleteKidComment(commentId: number) {
    return request({
        url: `/user/articleComment/deleteKidComment/${commentId}`,
        method: 'delete' 
})}

export function deleteParentComment(commentId: number) {
    return request({
        url: `/user/articleComment/deleteParentComment/${commentId}`,
        method: 'delete' 
})}

export function likeComment(commentId: number) {
    return request({
        url: `/user/articleComment/like/${commentId}`,
        method: 'post' 
})}

export function unlikeComment(commentId: number) {
    return request({
        url: `/user/articleComment/unlike/${commentId}`,
        method: 'post' 
})} 
