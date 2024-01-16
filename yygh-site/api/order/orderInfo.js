import request from '@/utils/request'

const api_name = `/api/order/orderInfo`

export default {
  // 预约下单
  submitOrder(scheduleId, patientId) {
    return request({
      url: `${api_name}/auth/submitOrder/${scheduleId}/${patientId}`,
      method: 'post'
    })
  },
  // 订单列表
  getPageList(page, limit, orderQueryVo) {
    return request({
      url: `${api_name}/auth/${page}/${limit}`,
      method: 'get',
      params: orderQueryVo
    })
  },
  // 订单状态
  getStatusList() {
    return request({
      url: `${api_name}/auth/getStatusList`,
      method: 'get'
    })
  },
  // 订单详情
  getOrderInfo(orderId) {
    return request({
      url: `${api_name}/auth/getOrder/${orderId}`,
      method: 'get'
    })
  }
}
