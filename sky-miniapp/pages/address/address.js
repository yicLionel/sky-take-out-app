const { request } = require('../../utils/request')

Page({
  data: {
    list: [],
    form: {
      consignee: '',
      sex: '先生',
      phone: '',
      provinceName: '',
      cityName: '',
      districtName: '',
      detail: ''
    }
  },

  onShow() {
    this.loadList()
  },

  loadList() {
    request({ url: '/user/addressBook/list' }).then((list) => {
      this.setData({ list })
    })
  },

  onInput(event) {
    const key = event.currentTarget.dataset.key
    this.setData({
      [`form.${key}`]: event.detail.value
    })
  },

  save() {
    const form = this.data.form
    if (!form.consignee || !form.phone || !form.detail) {
      wx.showToast({ title: '请补全地址', icon: 'none' })
      return
    }
    request({
      url: '/user/addressBook',
      method: 'POST',
      data: {
        ...form,
        defaultStatus: 1
      }
    }).then(() => {
      wx.showToast({ title: '已保存', icon: 'success' })
      this.setData({
        form: {
          consignee: '',
          sex: '先生',
          phone: '',
          provinceName: '',
          cityName: '',
          districtName: '',
          detail: ''
        }
      })
      this.loadList()
    })
  },

  setDefault(event) {
    request({
      url: `/user/addressBook/default?id=${event.currentTarget.dataset.id}`,
      method: 'PUT'
    }).then(() => this.loadList())
  },

  remove(event) {
    request({
      url: `/user/addressBook?id=${event.currentTarget.dataset.id}`,
      method: 'DELETE'
    }).then(() => this.loadList())
  }
})
