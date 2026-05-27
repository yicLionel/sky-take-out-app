# 苍穹外卖 App 端

`sky-app` 是同一套代码同时支持 Android 和 iOS 的移动端入口。

## 运行方式

本地开发：

```bash
python3 -m http.server 5173
```

然后访问：

```text
http://localhost:5173/sky-app/index.html
```

## Android 和 iOS 支持

- Android：Chrome 打开后可通过“安装应用”添加到桌面，也可以作为 WebView/Capacitor 外壳的前端资源。
- iOS：Safari 打开后可通过“添加到主屏幕”以独立 App 样式运行。
- 业务接口统一复用后端 `http://localhost:8080/user/**`。
- 朋友端不会显示接口配置，打开链接后直接使用。

部署前，把 `config.js` 里的 `apiBaseUrl` 改成你的公网后端地址，例如：

```js
window.SKY_APP_CONFIG = {
  apiBaseUrl: 'https://api.example.com'
}
```

真机本地调试时不要使用 `localhost` 连接电脑后端，应临时改成电脑的局域网地址，例如 `http://192.168.1.10:8080`。

## 商家端

商家订单页面：

```text
http://localhost:5173/sky-app/admin.html
```

本地商家口令默认是：

```text
sky-admin-local-token
```

部署到云服务器时，必须把 `application.yml` 里的 `sky.admin.token` 改成你自己的强口令。朋友端下单后，订单会写入同一个云端 MySQL，你在商家端刷新即可看到。商家端可以手动改后端地址，朋友端不需要配置。

## Vercel 部署

Vercel 只部署 `sky-app` 静态前端。后端仍需要部署到公网服务器，例如 `https://api.example.com`。

部署前先改 `config.js`：

```js
window.SKY_APP_CONFIG = {
  apiBaseUrl: 'https://api.example.com'
}
```

在 Vercel 创建项目时：

- Root Directory：`sky-app`
- Framework Preset：`Other`
- Build Command：留空
- Output Directory：留空

部署后：

- 朋友端：`https://你的-vercel-域名.vercel.app`
- 商家端：`https://你的-vercel-域名.vercel.app/admin`
