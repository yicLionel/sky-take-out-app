const { request } = require('../../utils/request')

Page({
  data: {
    loading: false
  },

  onLoad() {
    if (wx.getStorageSync('token')) {
      wx.switchTab({ url: '/pages/menu/menu' })
    }
  },

  login() {
    this.setData({ loading: true })
    wx.login({
      success: (res) => this.doLogin(res.code || 'dev-code'),
      fail: () => this.doLogin('dev-code')
    })
  },

  doLogin(code) {
    request({
      url: '/user/login',
      method: 'POST',
      data: { code }
    }).then((data) => {
      wx.setStorageSync('token', data.token)
      wx.setStorageSync('user', data)
      wx.switchTab({ url: '/pages/menu/menu' })
    }).finally(() => {
      this.setData({ loading: false })
    })
  }
})
