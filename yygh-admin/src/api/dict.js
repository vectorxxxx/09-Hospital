import request from '@/utils/request'

const api_name = '/admin/cmn/dict'

export default {
  // 根据数据id查询子数据列表
  dictList(id) {
    return request({
      url: `${api_name}/findChildData/${id}`,
      method: 'get'
    })
  }
}
