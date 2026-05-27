const state = {
  baseUrl: window.SKY_APP_CONFIG?.apiBaseUrl || 'http://localhost:8080',
  token: localStorage.getItem('skyToken') || '',
  user: JSON.parse(localStorage.getItem('skyUser') || 'null'),
  view: location.hash.replace('#/', '') || 'menu',
  categories: [],
  activeCategoryId: null,
  activeType: 1,
  items: [],
  cart: [],
  addresses: [],
  orders: [],
  shopOpen: true,
  remark: '',
  orderDetail: null
}

const app = document.querySelector('#app')
const toast = document.querySelector('#toast')

if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('./service-worker.js').catch(() => {})
  })
}

window.addEventListener('hashchange', () => {
  state.view = location.hash.replace('#/', '') || 'menu'
  route()
})

document.addEventListener('click', (event) => {
  const action = event.target.closest('[data-action]')
  if (!action) return
  actions[action.dataset.action](action.dataset)
})

document.addEventListener('input', (event) => {
  const input = event.target
  if (input.matches('[data-bind]')) {
    state[input.dataset.bind] = input.value
  }
})

function api(path, options = {}) {
  return fetch(state.baseUrl + path, {
    method: options.method || 'GET',
    headers: {
      token: state.token,
      'content-type': 'application/json'
    },
    body: options.body ? JSON.stringify(options.body) : undefined
  })
    .then(async (res) => {
      const body = await res.json().catch(() => ({}))
      if (res.status === 401 || body.msg === 'NOT_LOGIN') {
        logout(true)
        throw new Error('请先登录')
      }
      if (body.code === 1) return body.data
      throw new Error(body.msg || '请求失败')
    })
    .catch((error) => {
      showToast(error.message === 'Failed to fetch' ? '后端未连接' : error.message)
      throw error
    })
}

function route() {
  if (!state.token && state.view !== 'login') {
    navigate('login')
    return
  }
  if (state.view === 'login') renderLogin()
  if (state.view === 'menu') loadMenu()
  if (state.view === 'cart') loadCart(true)
  if (state.view === 'checkout') loadCheckout()
  if (state.view === 'address') loadAddress()
  if (state.view === 'orders') loadOrders()
  if (state.view.startsWith('order-detail')) loadOrderDetail()
  if (state.view === 'profile') renderProfile()
}

function navigate(view) {
  location.hash = '#/' + view
}

function showToast(message) {
  toast.textContent = message
  toast.hidden = false
  clearTimeout(showToast.timer)
  showToast.timer = setTimeout(() => {
    toast.hidden = true
  }, 1800)
}

function imageUrl(path) {
  if (!path) return ''
  if (path.startsWith('http')) return path
  return state.baseUrl + path
}

function itemCountText(count) {
  return count ? `${count} 件` : '购物车'
}

function statusText(status) {
  if (status === 1) return '待支付'
  if (status === 2) return '已支付'
  if (status === 3) return '已完成'
  return '已取消'
}

function navHtml(active) {
  const tabs = [
    ['menu', '点餐'],
    ['cart', '购物车'],
    ['orders', '订单'],
    ['profile', '我的']
  ]
  return `
    <nav class="bottom-nav">
      ${tabs.map(([view, text]) => `
        <button class="nav-btn ${active === view ? 'active' : ''}" data-action="nav" data-view="${view}">
          ${text}
        </button>
      `).join('')}
    </nav>
  `
}

function renderLogin() {
  app.innerHTML = `
    <section class="login-hero">
      <h1>颜小厨爱心厨房</h1>
      <p>App 端点餐、购物车、地址和订单闭环。</p>
      <div class="login-panel">
        <button class="primary-btn" data-action="login">一键登录</button>
      </div>
    </section>
  `
}

function renderShell(content, active = 'menu') {
  app.innerHTML = `<section class="screen">${content}</section>${navHtml(active)}`
}

function loadMenu() {
  Promise.all([
    api('/user/shop/status'),
    api('/user/category/list'),
    api('/user/shoppingCart/list')
  ]).then(([status, categories, cart]) => {
    state.shopOpen = status === 1
    state.categories = categories || []
    state.cart = cart || []
    const first = state.categories[0] || {}
    if (!state.activeCategoryId) {
      state.activeCategoryId = first.id || null
      state.activeType = first.type || 1
    }
    if (state.activeCategoryId) {
      const category = state.categories.find((item) => item.id === state.activeCategoryId) || first
      return loadItems(category.id, category.type)
    }
    renderMenu()
    return null
  })
}

function loadItems(categoryId, type) {
  const url = type === 2 ? '/user/setmeal/list' : '/user/dish/list'
  state.activeCategoryId = Number(categoryId)
  state.activeType = Number(type)
  return api(`${url}?categoryId=${categoryId}`).then((items) => {
    state.items = (items || []).map((item) => {
      const firstFlavor = (item.flavors || [])[0]
      const flavorValue = firstFlavor ? firstFlavor.value.split(',')[0] : ''
      return {
        ...item,
        flavorText: firstFlavor ? `${firstFlavor.name}: ${flavorValue}` : '',
        defaultFlavor: firstFlavor ? `${firstFlavor.name}:${flavorValue}` : ''
      }
    })
    renderMenu()
  })
}

function renderMenu() {
  const cartCount = state.cart.reduce((sum, item) => sum + item.number, 0)
  const content = `
    <header class="topbar">
      <div class="title-block">
        <h1>点餐</h1>
        <p>选择菜品后加入购物车</p>
      </div>
      <span class="status-pill ${state.shopOpen ? '' : 'closed'}">${state.shopOpen ? '营业中' : '休息中'}</span>
    </header>
    <div class="layout">
      <aside class="category-list">
        ${state.categories.map((category) => `
          <button class="category-btn ${category.id === state.activeCategoryId ? 'active' : ''}"
            data-action="category" data-id="${category.id}" data-type="${category.type}">
            ${category.name}
          </button>
        `).join('')}
      </aside>
      <section class="item-list">
        ${state.items.length ? state.items.map((item) => `
          <article class="item-card">
            <img class="food-img" src="${imageUrl(item.image)}" alt="${item.name}" />
            <div class="item-main">
              <h3>${item.name}</h3>
              <p class="desc">${item.description || item.flavorText || '经典热销'}</p>
              <div class="item-foot">
                <button class="icon-btn" data-action="addItem" data-id="${item.id}" aria-label="加入购物车">+</button>
              </div>
            </div>
          </article>
        `).join('') : '<div class="empty">暂无商品</div>'}
      </section>
    </div>
    <div class="fab-cart">
      <span>${itemCountText(cartCount)}</span>
      <button data-action="nav" data-view="cart">去结算</button>
    </div>
  `
  renderShell(content, 'menu')
}

function loadCart(render = false) {
  return api('/user/shoppingCart/list').then((cart) => {
    state.cart = cart || []
    if (render) renderCart()
  })
}

function renderCart() {
  const content = `
    <header class="topbar">
      <h1 class="page-title">购物车</h1>
      <button class="danger-btn" data-action="cleanCart">清空</button>
    </header>
    <section class="cart-list">
      ${state.cart.length ? state.cart.map((item, index) => `
        <article class="cart-card">
          <img class="food-img" src="${imageUrl(item.image)}" alt="${item.name}" />
          <div class="cart-main">
            <h3>${item.name}</h3>
            <p class="desc">${item.dishFlavor || '默认规格'}</p>
            <div class="cart-foot">
              <span class="qty">
                <button data-action="subCart" data-index="${index}">-</button>
                ${item.number}
                <button data-action="addCart" data-index="${index}">+</button>
              </span>
            </div>
          </div>
        </article>
      `).join('') : '<div class="empty">购物车为空</div>'}
    </section>
    <section class="summary-card">
      <div class="total-row">
        <strong>${itemCountText(state.cart.reduce((sum, item) => sum + item.number, 0))}</strong>
        <button class="ghost-btn" data-action="nav" data-view="checkout">提交订单</button>
      </div>
    </section>
  `
  renderShell(content, 'cart')
}

function loadCheckout() {
  Promise.all([
    api('/user/addressBook/default'),
    api('/user/shoppingCart/list')
  ]).then(([address, cart]) => {
    state.defaultAddress = address
    state.cart = cart || []
    renderCheckout()
  })
}

function renderCheckout() {
  const address = state.defaultAddress
  const content = `
    <header class="topbar">
      <h1 class="page-title">确认订单</h1>
      <button class="ghost-btn" data-action="nav" data-view="address">地址</button>
    </header>
    <section class="summary-card">
      ${address ? `
        <strong>${address.consignee} ${address.phone}</strong>
        <p class="address-meta">${address.provinceName || ''}${address.cityName || ''}${address.districtName || ''}${address.detail || ''}</p>
      ` : '<p class="address-meta">请先新增收货地址</p>'}
    </section>
    <h2 class="section-title">商品</h2>
    <section class="summary-card">
      ${state.cart.map((item) => `
        <div class="detail-line">
          <span>${item.name} x${item.number}</span>
        </div>
      `).join('') || '<div class="empty">购物车为空</div>'}
    </section>
    <label class="field">
      <span>备注</span>
      <textarea data-bind="remark">${state.remark || ''}</textarea>
    </label>
    <button class="primary-btn" data-action="submitOrder">提交订单</button>
  `
  renderShell(content, 'cart')
}

function loadAddress() {
  api('/user/addressBook/list').then((addresses) => {
    state.addresses = addresses || []
    renderAddress()
  })
}

function renderAddress() {
  const content = `
    <header class="topbar">
      <h1 class="page-title">地址</h1>
      <button class="ghost-btn" data-action="nav" data-view="checkout">返回结算</button>
    </header>
    <section class="summary-card">
      <div class="form-grid">
        <label class="field"><span>联系人</span><input id="consignee" /></label>
        <label class="field"><span>手机号</span><input id="phone" /></label>
        <label class="field"><span>省</span><input id="provinceName" /></label>
        <label class="field"><span>市</span><input id="cityName" /></label>
        <label class="field"><span>区</span><input id="districtName" /></label>
        <label class="field"><span>称呼</span><input id="sex" value="先生" /></label>
        <label class="field wide"><span>详细地址</span><input id="detail" /></label>
      </div>
      <button class="primary-btn" data-action="saveAddress">保存为默认地址</button>
    </section>
    <h2 class="section-title">已有地址</h2>
    <section class="address-list">
      ${state.addresses.length ? state.addresses.map((address) => `
        <article class="address-card">
          <strong>${address.consignee} ${address.phone}</strong>
          <p class="address-meta">${address.provinceName || ''}${address.cityName || ''}${address.districtName || ''}${address.detail || ''}</p>
          <div class="card-actions">
            <span>${address.defaultStatus === 1 ? '默认地址' : ''}</span>
            <span>
              <button class="ghost-btn" data-action="defaultAddress" data-id="${address.id}">设默认</button>
              <button class="danger-btn" data-action="deleteAddress" data-id="${address.id}">删除</button>
            </span>
          </div>
        </article>
      `).join('') : '<div class="empty">暂无地址</div>'}
    </section>
  `
  renderShell(content, 'profile')
}

function loadOrders() {
  api('/user/order/historyOrders').then((orders) => {
    state.orders = orders || []
    renderOrders()
  })
}

function renderOrders() {
  const content = `
    <header class="topbar">
      <h1 class="page-title">订单</h1>
    </header>
    <section class="order-list">
      ${state.orders.length ? state.orders.map((order) => `
        <article class="order-card" data-action="orderDetail" data-id="${order.id}">
          <div class="total-row">
            <strong>${order.number}</strong>
            <span class="status-pill">${statusText(order.status)}</span>
          </div>
          <p class="order-meta">${order.orderTime || ''}</p>
          <div class="total-row">
            <span>${order.consignee || ''}</span>
          </div>
        </article>
      `).join('') : '<div class="empty">暂无订单</div>'}
    </section>
  `
  renderShell(content, 'orders')
}

function loadOrderDetail() {
  const id = state.view.split('/')[1]
  api(`/user/order/orderDetail/${id}`).then((detail) => {
    state.orderDetail = detail
    renderOrderDetail()
  })
}

function renderOrderDetail() {
  const order = state.orderDetail
  const content = `
    <header class="topbar">
      <h1 class="page-title">订单详情</h1>
      <button class="ghost-btn" data-action="nav" data-view="orders">返回</button>
    </header>
    <section class="summary-card">
      <div class="total-row">
        <strong>${order.number}</strong>
        <span class="status-pill">${statusText(order.status)}</span>
      </div>
      <p class="order-meta">${order.address || ''}</p>
      <p class="order-meta">${order.consignee || ''} ${order.phone || ''}</p>
    </section>
    <h2 class="section-title">明细</h2>
    <section class="summary-card">
      ${(order.orderDetails || []).map((item) => `
        <div class="detail-line">
          <span>${item.name} x${item.number}</span>
        </div>
      `).join('')}
    </section>
  `
  renderShell(content, 'orders')
}

function renderProfile() {
  const content = `
    <header class="topbar">
      <div class="title-block">
        <h1>我的</h1>
        <p>${state.user ? state.user.openid : '当前用户'}</p>
      </div>
    </header>
    <section class="summary-card">
      <button class="ghost-btn" data-action="nav" data-view="address">地址管理</button>
      <button class="ghost-btn" data-action="nav" data-view="orders">订单记录</button>
      <button class="danger-btn" data-action="logout">退出登录</button>
    </section>
  `
  renderShell(content, 'profile')
}

function cartDTO(item) {
  return item.dishId
    ? { dishId: item.dishId, dishFlavor: item.dishFlavor || '' }
    : { setmealId: item.setmealId }
}

function logout(silent = false) {
  state.token = ''
  state.user = null
  localStorage.removeItem('skyToken')
  localStorage.removeItem('skyUser')
  if (!silent) showToast('已退出')
  navigate('login')
}

const actions = {
  nav(dataset) {
    navigate(dataset.view)
  },
  login() {
    api('/user/login', { method: 'POST', body: {} }).then((user) => {
      state.user = user
      state.token = user.token
      localStorage.setItem('skyToken', user.token)
      localStorage.setItem('skyUser', JSON.stringify(user))
      navigate('menu')
    })
  },
  category(dataset) {
    loadItems(dataset.id, dataset.type)
  },
  addItem(dataset) {
    const item = state.items.find((entry) => entry.id === Number(dataset.id))
    const body = state.activeType === 2
      ? { setmealId: item.id }
      : { dishId: item.id, dishFlavor: item.defaultFlavor || '' }
    api('/user/shoppingCart/add', { method: 'POST', body })
      .then(() => loadCart())
      .then(() => {
        showToast('已加入')
        renderMenu()
      })
  },
  addCart(dataset) {
    api('/user/shoppingCart/add', { method: 'POST', body: cartDTO(state.cart[dataset.index]) })
      .then(() => loadCart(true))
  },
  subCart(dataset) {
    api('/user/shoppingCart/sub', { method: 'POST', body: cartDTO(state.cart[dataset.index]) })
      .then(() => loadCart(true))
  },
  cleanCart() {
    api('/user/shoppingCart/clean', { method: 'DELETE' }).then(() => loadCart(true))
  },
  saveAddress() {
    const keys = ['consignee', 'sex', 'phone', 'provinceName', 'cityName', 'districtName', 'detail']
    const body = {}
    keys.forEach((key) => {
      body[key] = document.querySelector(`#${key}`).value.trim()
    })
    body.defaultStatus = 1
    if (!body.consignee || !body.phone || !body.detail) {
      showToast('请补全地址')
      return
    }
    api('/user/addressBook', { method: 'POST', body }).then(() => {
      showToast('已保存')
      loadAddress()
    })
  },
  defaultAddress(dataset) {
    api(`/user/addressBook/default?id=${dataset.id}`, { method: 'PUT' }).then(() => loadAddress())
  },
  deleteAddress(dataset) {
    api(`/user/addressBook?id=${dataset.id}`, { method: 'DELETE' }).then(() => loadAddress())
  },
  submitOrder() {
    if (!state.defaultAddress) {
      showToast('请先新增地址')
      return
    }
    if (!state.cart.length) {
      showToast('购物车为空')
      return
    }
    api('/user/order/submit', {
      method: 'POST',
      body: {
        addressBookId: state.defaultAddress.id,
        remark: state.remark,
        tablewareNumber: 1,
        tablewareStatus: 1
      }
    })
      .then((order) => api('/user/order/payment', {
        method: 'POST',
        body: { orderId: order.id }
      }).then(() => order))
      .then((order) => navigate(`order-detail/${order.id}`))
  },
  orderDetail(dataset) {
    navigate(`order-detail/${dataset.id}`)
  },
  logout() {
    logout()
  }
}

route()
