const { request } = require('../../utils/request')

Page({
  data: {
    address: null,
    items: [],
    total: '0.00',
    remark: '',
    submitting: false
  },

  onShow() {
    this.loadAddress()
    this.loadCart()
  },

  loadAddress() {
    request({ url: '/user/addressBook/default' }).then((address) => {
      this.setData({ address })
    })
  },

  loadCart() {
    request({ url: '/user/shoppingCart/list' }).then((items) => {
      const mapped = items.map((item) => ({
        ...item,
        lineTotal: (Number(item.amount) * item.number).toFixed(2)
      }))
      const total = mapped.reduce((sum, item) => sum + Number(item.lineTotal), 0)
      this.setData({
        items: mapped,
        total: total.toFixed(2)
      })
    })
  },

  onRemarkInput(event) {
    this.setData({ remark: event.detail.value })
  },

  goAddress() {
    wx.navigateTo({ url: '/pages/address/address' })
  },

  submit() {
    if (!this.data.address) {
      wx.showToast({ title: '请先新增地址', icon: 'none' })
      return
    }
    if (this.data.items.length === 0) {
      wx.showToast({ title: '购物车为空', icon: 'none' })
      return
    }
    this.setData({ submitting: true })
    request({
      url: '/user/order/submit',
      method: 'POST',
      data: {
        addressBookId: this.data.address.id,
        remark: this.data.remark,
        tablewareNumber: 1,
        tablewareStatus: 1
      }
    }).then((order) => {
      return request({
        url: '/user/order/payment',
        method: 'POST',
        data: { orderId: order.id }
      }).then(() => order)
    }).then((order) => {
      wx.redirectTo({ url: `/pages/order-detail/order-detail?id=${order.id}` })
    }).finally(() => {
      this.setData({ submitting: false })
    })
  }
})
