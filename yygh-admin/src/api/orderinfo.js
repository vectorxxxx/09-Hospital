import request from '@/utils/request'

const api_name = '/admin/order/orderInfo'

export default {
  // 订单列表（条件查询带分页）
  getPageList(page, limit, orderQueryVo) {
    return request({
      url: `${api_name}/${page}/${limit}`,
      method: 'get',
      params: orderQueryVo
    })
  },
  // 获取订单状态
  getStatusList() {
    return request({
      url: `${api_name}/getStatusList`,
      method: 'get'
    })
  },
  // 获取订单详情
  getOrderInfo(id) {
    return request({
      url: `${api_name}/show/${id}`,
      method: 'get'
    })
  }
}
