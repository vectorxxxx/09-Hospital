import request from '@/utils/request'

const api_name = '/api/sms'

export default {
  // 发送验证码
  sendCode(phone) {
    return request({
      url: `${api_name}/send/${phone}`,
      method: 'get'
    })
  }
}
