# Railway + Vercel 部署

## 架构

- Railway：运行 `sky-server` 后端、MySQL、Redis。
- Vercel：托管 `sky-app` 静态前端。

## Railway 后端

1. 把本项目推到 GitHub。
2. Railway 新建项目，选择 Deploy from GitHub repo。
3. 选择本仓库，Railway 会读取根目录 `Dockerfile` 构建 Spring Boot 后端。
4. 在 Railway 项目里添加 MySQL 服务。
5. 后端服务 Variables 添加：

```text
MYSQLHOST=${{MySQL.MYSQLHOST}}
MYSQLPORT=${{MySQL.MYSQLPORT}}
MYSQLDATABASE=${{MySQL.MYSQLDATABASE}}
MYSQLUSER=${{MySQL.MYSQLUSER}}
MYSQLPASSWORD=${{MySQL.MYSQLPASSWORD}}
JWT_SECRET=换成一串长随机字符串
ADMIN_TOKEN=换成你的商家强口令
SERVER_PORT=8080
```

6. 后端服务 Settings -> Networking -> Generate Domain，得到类似：

```text
https://xxx.up.railway.app
```

## 初始化 MySQL 数据

Railway MySQL 不会自动执行本仓库里的 `schema.sql` 和 `data.sql`。需要在 Railway MySQL 控制台或外部 MySQL 客户端执行：

```text
sky-server/src/main/resources/schema.sql
sky-server/src/main/resources/data.sql
```

## Vercel 前端

部署前先把 `sky-app/config.js` 改成 Railway 后端地址：

```js
window.SKY_APP_CONFIG = {
  apiBaseUrl: 'https://xxx.up.railway.app'
}
```

Vercel 创建项目时：

```text
Root Directory: sky-app
Framework Preset: Other
Build Command: 留空
Output Directory: 留空
```

部署后：

```text
朋友点单：https://你的-vercel域名.vercel.app
商家看单：https://你的-vercel域名.vercel.app/admin
```
