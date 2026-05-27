const { request } = require('../../utils/request')

Page({
  data: {
    order: null,
    statusText: ''
  },

  onLoad(query) {
    this.loadDetail(query.id)
  },

  loadDetail(id) {
    request({ url: `/user/order/orderDetail/${id}` }).then((order) => {
      this.setData({
        order,
        statusText: this.statusText(order.status)
      })
    })
  },

  statusText(status) {
    if (status === 1) return '待支付'
    if (status === 2) return '已支付'
    if (status === 3) return '已完成'
    return '已取消'
  }
})
