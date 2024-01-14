import request from '@/utils/request'

const api_name = `/api/user/patient`

export default {
  // 获取就诊人列表
  findList() {
    return request({
      url: `${api_name}/auth/findAll`,
      method: `get`
    })
  },

  // 根据ID获取就诊人信息
  getById(id) {
    return request({
      url: `${api_name}/auth/get/${id}`,
      method: 'get'
    })
  },

  // 添加就诊人
  save(patient) {
    return request({
      url: `${api_name}/auth/save`,
      method: 'post',
      data: patient
    })
  },

  // 更新就诊人信息
  updateById(patient) {
    return request({
      url: `${api_name}/auth/update`,
      method: 'put',
      data: patient
    })
  },

  // 删除就诊人信息
  removeById(id) {
    return request({
      url: `${api_name}/auth/remove/${id}`,
      method: 'delete'
    })
  }
}
