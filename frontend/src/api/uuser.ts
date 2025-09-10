import request from '@/utils/request'

/**
 * 用户登录数据传输对象
 */
export interface UserLoginDTO {
  account: string
  password: string
}

/**
 * 用户注册数据传输对象
 */
export interface UserRegisterDTO {
  username: string
  email: string
  password: string
}

/**
 * 用户登录返回对象
 */
export interface UserLoginVO {
  id: number
  token: string
}

/**
 * 用户信息对象
 */
export interface User {
  id: number
  account: string
  password: string
  username: string
  avatar: string
  gender: number
  region: string
  signature: string
  createdAt: string
  cover: string
  admin: number
  status: number
  email: string
}

/**
 * 统一返回结果
 */
export interface Result<T> {
  code: number
  msg: string
  data: T
}

/**
 * 用户登录
 * @param data 登录数据
 * @returns 登录结果
 */
export function login(data: UserLoginDTO) {
  return request<Result<UserLoginVO>>({
    url: '/user/user/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * @param data 注册数据
 * @returns 注册结果
 */
export function register(data: UserRegisterDTO) {
  return request<Result<null>>({
    url: '/user/user/register',
    method: 'post',
    data
  })
}

/**
 * 根据ID获取用户信息
 * @param id 用户ID
 * @returns 用户信息
 */
export function getUserById(id: number) {
  return request<Result<User>>({
    url: `/user/user/getById/${id}`,
    method: 'get'
  })
}

