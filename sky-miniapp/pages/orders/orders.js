const { request } = require('../../utils/request')

Page({
  data: {
    orders: []
  },

  onShow() {
    this.loadOrders()
  },

  loadOrders() {
    request({ url: '/user/order/historyOrders' }).then((orders) => {
      this.setData({
        orders: orders.map((item) => ({
          ...item,
          statusText: this.statusText(item.status)
        }))
      })
    })
  },

  statusText(status) {
    if (status === 1) return '待支付'
    if (status === 2) return '已支付'
    if (status === 3) return '已完成'
    return '已取消'
  },

  goDetail(event) {
    wx.navigateTo({ url: `/pages/order-detail/order-detail?id=${event.currentTarget.dataset.id}` })
  }
})
