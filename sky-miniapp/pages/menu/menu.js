const { request, baseUrl } = require('../../utils/request')

Page({
  data: {
    shopOpen: true,
    categories: [],
    activeCategoryId: null,
    activeType: 1,
    items: [],
    cartCount: 0
  },

  onShow() {
    if (!wx.getStorageSync('token')) {
      wx.redirectTo({ url: '/pages/login/login' })
      return
    }
    this.loadShop()
    this.loadCategories()
    this.loadCartCount()
  },

  loadShop() {
    request({ url: '/user/shop/status' }).then((status) => {
      this.setData({ shopOpen: status === 1 })
    })
  },

  loadCategories() {
    request({ url: '/user/category/list' }).then((categories) => {
      const first = categories[0] || {}
      this.setData({
        categories,
        activeCategoryId: first.id || null,
        activeType: first.type || 1
      })
      if (first.id) {
        this.loadItems(first.id, first.type)
      }
    })
  },

  selectCategory(event) {
    const id = Number(event.currentTarget.dataset.id)
    const category = this.data.categories.find((item) => item.id === id)
    this.setData({
      activeCategoryId: id,
      activeType: category.type
    })
    this.loadItems(id, category.type)
  },

  loadItems(categoryId, type) {
    const url = type === 2 ? '/user/setmeal/list' : '/user/dish/list'
    request({ url: `${url}?categoryId=${categoryId}` }).then((items) => {
      const mapped = items.map((item) => {
        const flavors = item.flavors || []
        const firstFlavor = flavors[0]
        const flavorValue = firstFlavor ? firstFlavor.value.split(',')[0] : ''
        return {
          ...item,
          fullImage: baseUrl() + item.image,
          flavorText: firstFlavor ? `${firstFlavor.name}: ${flavorValue}` : '',
          defaultFlavor: firstFlavor ? `${firstFlavor.name}:${flavorValue}` : ''
        }
      })
      this.setData({ items: mapped })
    })
  },

  addItem(event) {
    const id = Number(event.currentTarget.dataset.id)
    const item = this.data.items.find((entry) => entry.id === id)
    const data = this.data.activeType === 2
      ? { setmealId: item.id }
      : { dishId: item.id, dishFlavor: item.defaultFlavor || '' }
    request({
      url: '/user/shoppingCart/add',
      method: 'POST',
      data
    }).then(() => {
      wx.showToast({ title: '已加入', icon: 'success' })
      this.loadCartCount()
    })
  },

  loadCartCount() {
    request({ url: '/user/shoppingCart/list' }).then((list) => {
      const count = list.reduce((sum, item) => sum + item.number, 0)
      this.setData({ cartCount: count })
    })
  },

  goCart() {
    wx.switchTab({ url: '/pages/cart/cart' })
  }
})
