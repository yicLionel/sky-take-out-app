DROP TABLE IF EXISTS order_detail;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS shopping_cart;
DROP TABLE IF EXISTS address_book;
DROP TABLE IF EXISTS setmeal_dish;
DROP TABLE IF EXISTS setmeal;
DROP TABLE IF EXISTS dish_flavor;
DROP TABLE IF EXISTS dish;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  openid VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(64),
  phone VARCHAR(32),
  avatar VARCHAR(255),
  create_time DATETIME NOT NULL
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  type INT NOT NULL,
  name VARCHAR(64) NOT NULL,
  sort INT NOT NULL DEFAULT 0,
  status INT NOT NULL DEFAULT 1
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dish (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  category_id BIGINT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  image VARCHAR(255),
  description VARCHAR(255),
  status INT NOT NULL DEFAULT 1
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dish_flavor (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dish_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  `value` VARCHAR(255) NOT NULL
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE setmeal (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  image VARCHAR(255),
  description VARCHAR(255),
  status INT NOT NULL DEFAULT 1
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE setmeal_dish (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  setmeal_id BIGINT NOT NULL,
  dish_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  copies INT NOT NULL DEFAULT 1
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE address_book (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  consignee VARCHAR(64) NOT NULL,
  sex VARCHAR(8),
  phone VARCHAR(32) NOT NULL,
  province_name VARCHAR(64),
  city_name VARCHAR(64),
  district_name VARCHAR(64),
  detail VARCHAR(255) NOT NULL,
  default_status INT NOT NULL DEFAULT 0
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE shopping_cart (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  image VARCHAR(255),
  dish_id BIGINT,
  setmeal_id BIGINT,
  dish_flavor VARCHAR(255),
  number INT NOT NULL DEFAULT 1,
  amount DECIMAL(10,2) NOT NULL,
  create_time DATETIME NOT NULL
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `orders` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  number VARCHAR(64) NOT NULL UNIQUE,
  status INT NOT NULL,
  user_id BIGINT NOT NULL,
  address_book_id BIGINT NOT NULL,
  order_time DATETIME NOT NULL,
  checkout_time DATETIME,
  pay_status INT NOT NULL DEFAULT 0,
  amount DECIMAL(10,2) NOT NULL,
  remark VARCHAR(255),
  phone VARCHAR(32) NOT NULL,
  address VARCHAR(255) NOT NULL,
  consignee VARCHAR(64) NOT NULL,
  cancel_reason VARCHAR(255),
  rejection_reason VARCHAR(255),
  delivery_time DATETIME,
  estimated_delivery_time DATETIME,
  pack_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
  tableware_number INT,
  tableware_status INT
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE order_detail (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  image VARCHAR(255),
  order_id BIGINT NOT NULL,
  dish_id BIGINT,
  setmeal_id BIGINT,
  dish_flavor VARCHAR(255),
  number INT NOT NULL,
  amount DECIMAL(10,2) NOT NULL
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
