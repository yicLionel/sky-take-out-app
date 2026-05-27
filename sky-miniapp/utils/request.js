function baseUrl() {
  return getApp().globalData.baseUrl
}

function request(options) {
  const token = wx.getStorageSync('token') || ''
  return new Promise((resolve, reject) => {
    wx.request({
      url: baseUrl() + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        token,
        'content-type': 'application/json'
      },
      success(res) {
        const body = res.data || {}
        if (res.statusCode === 401 || body.msg === 'NOT_LOGIN') {
          wx.removeStorageSync('token')
          wx.redirectTo({ url: '/pages/login/login' })
          reject(body)
          return
        }
        if (body.code === 1) {
          resolve(body.data)
          return
        }
        wx.showToast({ title: body.msg || '请求失败', icon: 'none' })
        reject(body)
      },
      fail(err) {
        wx.showToast({ title: '后端未连接', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = {
  request,
  baseUrl
}
