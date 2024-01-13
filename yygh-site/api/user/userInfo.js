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
  }
}
