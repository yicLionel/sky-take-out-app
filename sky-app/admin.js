const state = {
  baseUrl: localStorage.getItem('skyAdminBaseUrl') || window.SKY_APP_CONFIG?.apiBaseUrl || 'http://localhost:8080',
  adminToken: localStorage.getItem('skyAdminToken') || '',
  orders: [],
  activeOrder: null,
  knownOrderIds: new Set(),
  pollingStarted: false,
  notifyOrder: null
}

if ('serviceWorker' in navigator) {
  navigator.serviceWorker.getRegistrations()
    .then((registrations) => Promise.all(registrations.map((registration) => registration.unregister())))
    .then(() => window.caches?.keys?.())
    .then((keys) => Promise.all((keys || []).map((key) => window.caches.delete(key))))
    .catch(() => {})
}

const admin = document.querySelector('#admin')
const toast = document.querySelector('#toast')
let pollTimer = null

document.addEventListener('click', (event) => {
  const action = event.target.closest('[data-action]')
  if (!action) return
  actions[action.dataset.action](action.dataset)
})

document.addEventListener('input', (event) => {
  if (event.target.matches('[data-bind]')) {
    state[event.target.dataset.bind] = event.target.value
  }
})

function api(path, options = {}) {
  return fetch(state.baseUrl + path, {
    method: options.method || 'GET',
    headers: {
      'X-Admin-Token': state.adminToken
    }
  })
    .then(async (res) => {
      const body = await res.json().catch(() => ({}))
      if (body.code === 1) return body.data
      if (body.msg === 'ADMIN_TOKEN_ERROR') {
        state.adminToken = ''
        localStorage.removeItem('skyAdminToken')
        throw new Error('商家口令错误，请重新输入')
      }
      throw new Error(body.msg || '请求失败')
    })
    .catch((error) => {
      showToast(error.message === 'Failed to fetch' ? '后端未连接' : error.message)
      throw error
    })
}

function showToast(message) {
  toast.textContent = message
  toast.hidden = false
  clearTimeout(showToast.timer)
  showToast.timer = setTimeout(() => {
    toast.hidden = true
  }, 1800)
}

function startOrderPolling() {
  if (state.pollingStarted) return
  state.pollingStarted = true
  clearInterval(pollTimer)
  pollTimer = setInterval(checkNewOrders, 15000)
}

function stopOrderPolling() {
  state.pollingStarted = false
  clearInterval(pollTimer)
  pollTimer = null
}

function rememberOrders(orders) {
  orders.forEach((order) => state.knownOrderIds.add(Number(order.id)))
}

function checkNewOrders() {
  if (!state.adminToken) return
  api('/admin/order/list')
    .then((orders) => {
      const nextOrders = orders || []
      const newOrders = nextOrders.filter((order) => !state.knownOrderIds.has(Number(order.id)))
      state.orders = nextOrders
      rememberOrders(nextOrders)
      if (newOrders.length) {
        state.notifyOrder = newOrders[0]
        renderOrders()
      }
    })
    .catch(() => {
      stopOrderPolling()
      renderLogin()
    })
}

function statusText(status) {
  if (status === 1) return '待支付'
  if (status === 2) return '已支付'
  if (status === 3) return '已完成'
  return '已取消'
}

function renderLogin() {
  stopOrderPolling()
  admin.innerHTML = `
    <section class="screen no-tab">
      <header class="topbar">
        <div class="title-block">
          <h1>商家订单</h1>
          <p>查看朋友下单信息</p>
        </div>
      </header>
      <section class="summary-card">
        <label class="field">
          <span>后端地址</span>
          <input data-bind="baseUrl" value="${state.baseUrl}" />
        </label>
        <label class="field">
          <span>商家口令</span>
          <input data-bind="adminToken" value="${state.adminToken}" />
        </label>
        <button class="primary-btn" data-action="saveAdmin">进入商家端</button>
      </section>
    </section>
  `
}

function loadOrders() {
  renderLoading()
  api('/admin/order/list')
    .then((orders) => {
      state.orders = orders || []
      rememberOrders(state.orders)
      renderOrders()
      startOrderPolling()
    })
    .catch(() => {
      renderLogin()
    })
}

function renderLoading() {
  admin.innerHTML = `
    <section class="screen no-tab">
      <header class="topbar">
        <div class="title-block">
          <h1>商家订单</h1>
          <p>正在连接后端</p>
        </div>
      </header>
      <div class="empty">加载中...</div>
    </section>
  `
}

function renderOrders() {
  const paidCount = state.orders.filter((order) => order.status === 2).length
  admin.innerHTML = `
    <section class="screen no-tab">
      <header class="topbar">
        <div class="title-block">
          <h1>商家订单</h1>
          <p>${state.orders.length} 单 · 已支付 ${paidCount} 单</p>
        </div>
        <button class="ghost-btn" data-action="refresh">刷新</button>
      </header>
      <section class="order-list">
        ${state.orders.length ? state.orders.map((order) => `
          <article class="order-card">
            <div class="total-row">
              <strong>${order.number}</strong>
              <span class="status-pill">${statusText(order.status)}</span>
            </div>
            <p class="order-meta">${order.orderTime || ''}</p>
            <p class="order-meta">${order.consignee || ''} ${order.phone || ''}</p>
            <p class="order-meta">${order.address || ''}</p>
            <div class="detail-lines">
              ${(order.orderDetails || []).map((item) => `
                <div class="detail-line">
                  <span>${item.name} x${item.number}</span>
                </div>
              `).join('')}
            </div>
            <div class="total-row">
              <span>
                <button class="ghost-btn" data-action="setStatus" data-id="${order.id}" data-status="3">完成</button>
                <button class="danger-btn" data-action="setStatus" data-id="${order.id}" data-status="4">取消</button>
              </span>
            </div>
          </article>
        `).join('') : '<div class="empty">暂无订单</div>'}
      </section>
      ${state.notifyOrder ? renderOrderNotice(state.notifyOrder) : ''}
    </section>
  `
}

function renderOrderNotice(order) {
  return `
    <div class="notice-backdrop">
      <section class="notice-card" role="dialog" aria-modal="true" aria-label="新订单提醒">
        <div class="notice-badge">新订单</div>
        <h2>有朋友刚刚下单</h2>
        <p>${order.consignee || '顾客'} ${order.phone || ''}</p>
        <p>${order.address || ''}</p>
        <div class="detail-lines">
          ${(order.orderDetails || []).map((item) => `
            <div class="detail-line">
              <span>${item.name} x${item.number}</span>
            </div>
          `).join('')}
        </div>
        <button class="primary-btn" data-action="closeNotice">知道了</button>
      </section>
    </div>
  `
}

const actions = {
  saveAdmin() {
    localStorage.setItem('skyAdminBaseUrl', state.baseUrl)
    localStorage.setItem('skyAdminToken', state.adminToken)
    loadOrders()
  },
  refresh() {
    loadOrders()
  },
  closeNotice() {
    state.notifyOrder = null
    renderOrders()
  },
  setStatus(dataset) {
    api(`/admin/order/${dataset.id}/status?status=${dataset.status}`, { method: 'POST' })
      .then(() => {
        showToast('已更新')
        loadOrders()
      })
  }
}

if (state.adminToken) {
  loadOrders()
} else {
  renderLogin()
}
