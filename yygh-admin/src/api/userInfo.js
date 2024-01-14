import request from '@/utils/request'

const api_name = '/admin/user'

export default {
  // 用户列表
  getPageList(page, limit, searchObj) {
    return request({
      url: `${api_name}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  },

  // 用户锁定
  lockUser(id, status) {
    return request({
      url: `${api_name}/lock/${id}/${status}`,
      method: 'post'
    })
  },

  // 用户详情
  show(id) {
    return request({
      url: `${api_name}/show/${id}`,
      method: 'get'
    })
  },

  // 认证审批
  approve(userId, authStatus) {
    return request({
      url: `${api_name}/approve/${userId}/${authStatus}`,
      method: 'post'
    })
  }
}

