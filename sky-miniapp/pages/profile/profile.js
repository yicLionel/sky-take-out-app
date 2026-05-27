const { baseUrl } = require('../../utils/request')

Page({
  data: {
    avatar: ''
  },

  onShow() {
    this.setData({ avatar: baseUrl() + '/images/avatar.svg' })
  },

  goAddress() {
    wx.navigateTo({ url: '/pages/address/address' })
  },

  goOrders() {
    wx.switchTab({ url: '/pages/orders/orders' })
  },

  logout() {
    wx.removeStorageSync('token')
    wx.removeStorageSync('user')
    wx.redirectTo({ url: '/pages/login/login' })
  }
})
