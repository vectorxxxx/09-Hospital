import request from '@/utils/request'

const api_name = '/api/user'

export default {
  // 会员登录
  login(loginVo) {
    return request({
      url: `${api_name}/login`,
      method: 'post',
      data: loginVo
    })
  },
  // 用户认证
  saveUserAuth(userAuthVo) {
    return request({
      url: `${api_name}/auth/userAuth`,
      method: 'post',
      data: userAuthVo
    })
  },
  // 获取用户信息
  getUserInfo() {
    return request({
      url: `${api_name}/auth/getUserInfo`,
      method: 'get'
    })
  }
}
