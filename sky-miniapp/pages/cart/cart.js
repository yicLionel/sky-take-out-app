const { request, baseUrl } = require('../../utils/request')

Page({
  data: {
    items: [],
    total: '0.00'
  },

  onShow() {
    this.loadCart()
  },

  loadCart() {
    request({ url: '/user/shoppingCart/list' }).then((items) => {
      const mapped = items.map((item) => ({
        ...item,
        fullImage: baseUrl() + item.image
      }))
      const total = mapped.reduce((sum, item) => sum + Number(item.amount) * item.number, 0)
      this.setData({
        items: mapped,
        total: total.toFixed(2)
      })
    })
  },

  addItem(event) {
    const item = this.data.items[event.currentTarget.dataset.index]
    request({
      url: '/user/shoppingCart/add',
      method: 'POST',
      data: this.toCartDTO(item)
    }).then(() => this.loadCart())
  },

  subItem(event) {
    const item = this.data.items[event.currentTarget.dataset.index]
    request({
      url: '/user/shoppingCart/sub',
      method: 'POST',
      data: this.toCartDTO(item)
    }).then(() => this.loadCart())
  },

  toCartDTO(item) {
    return item.dishId
      ? { dishId: item.dishId, dishFlavor: item.dishFlavor || '' }
      : { setmealId: item.setmealId }
  },

  goCheckout() {
    wx.navigateTo({ url: '/pages/checkout/checkout' })
  }
})
