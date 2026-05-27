# 苍穹外卖移动端 MVP

这是一个从零复刻的“苍穹外卖”移动端 MVP。第一阶段聚焦用户端点餐闭环：微信小程序登录、浏览分类和菜品、购物车、地址、下单、模拟支付、订单记录。

## 项目结构

- `sky-common`：通用返回结果、上下文、JWT 工具、业务异常。
- `sky-pojo`：实体、DTO、VO。
- `sky-server`：Spring Boot 后端接口、MyBatis Mapper、业务服务。
- `sky-miniapp`：原生微信小程序用户端。
- `sky-app`：移动 App/PWA 用户端和商家订单页，同时支持 Android 和 iOS，不依赖构建工具，直接复用后端接口。
- `docker-compose.yml`：MySQL 8.4 和 Redis 7 本地运行环境。

## 本地启动

1. 在 Docker Desktop 中取消暂停。
2. 启动依赖：

```bash
docker compose up -d
```

3. 启动后端：

```bash
./scripts/run-server.sh
```

如果想手动分两步运行，也可以先构建依赖模块，再运行后端：

```bash
./mvnw -pl sky-server -am -DskipTests package
java -jar sky-server/target/sky-server-0.0.1-SNAPSHOT.jar
```

4. 选择一个移动端入口运行：

- 微信小程序：微信开发者工具导入 `sky-miniapp`，关闭“校验合法域名”，后端地址默认是 `http://localhost:8080`。
- App/PWA：浏览器打开 `sky-app/index.html`。Android 可安装到桌面；iOS 使用 Safari 的“添加到主屏幕”。朋友端不显示接口配置，接口地址统一从 `sky-app/config.js` 读取。
- 商家端：浏览器打开 `sky-app/admin.html`，后端地址默认是 `http://localhost:8080`，本地商家口令默认是 `sky-admin-local-token`。

## 给朋友使用的部署方式

要实现“朋友手机打开链接 → 添加到桌面 → 像 App 一样使用 → 下单写入你的云端 MySQL → 你打开商家订单页面查看”，需要把后端和 `sky-app` 放到公网：

1. 云服务器运行 MySQL、Redis 和 `sky-server`。
2. `sky-server/src/main/resources/application.yml` 中的数据库地址改为云端 MySQL，并把 `sky.admin.token` 改成强口令。
3. 用 Nginx、Vercel、Netlify 或对象存储静态网站托管 `sky-app`。
4. 把 `sky-app/config.js` 里的 `apiBaseUrl` 改成云端后端地址，例如 `https://api.example.com`。
5. 朋友打开 `https://你的域名/index.html`，不需要配置接口，直接点登录和下单。
6. 你打开 `https://你的域名/admin.html`，填写商家口令，即可查看订单。

也可以用 Vercel 部署 `sky-app` 静态前端。创建 Vercel 项目时把 Root Directory 设为 `sky-app`，部署后朋友端访问 Vercel 域名根路径，商家端访问 `/admin`。Spring Boot、MySQL 和 Redis 仍需要单独部署到公网服务器。

## 自动化部署脚本

后端服务器部署：

```bash
./scripts/deploy-server.sh user@your-server
```

首次部署后，登录服务器修改 `~/sky-take-out/.env` 中的 `MYSQL_ROOT_PASSWORD`、`JWT_SECRET`、`ADMIN_TOKEN`，再执行：

```bash
cd ~/sky-take-out
docker compose -f docker-compose.prod.yml up -d --build
```

Vercel 部署前端：

```bash
./scripts/deploy-vercel.sh https://api.example.com
```

这个命令会先把 [config.js](/Users/yyc/Documents/苍穹外卖/sky-app/config.js) 改成公网后端地址，再发布 `sky-app` 到 Vercel。

Railway + Vercel 的详细部署步骤见 [docs/DEPLOY_RAILWAY_VERCEL.md](/Users/yyc/Documents/苍穹外卖/docs/DEPLOY_RAILWAY_VERCEL.md)。

## 测试

```bash
./mvnw test
```

测试会使用 H2 的 MySQL 兼容模式，不依赖本地 MySQL。

## MVP 范围

- 已做：mock 登录、分类、菜品、套餐、购物车、地址、下单、模拟支付、订单历史；支持微信小程序端和 App/H5 端两个用户入口。
- 未做：管理端、真实微信登录/支付、OSS 上传、WebSocket 来单提醒、统计报表。
