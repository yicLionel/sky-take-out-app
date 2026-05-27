INSERT INTO category (id, type, name, sort, status) VALUES
  (1, 2, '热销套餐', 1, 1),
  (2, 1, '招牌主食', 2, 1),
  (3, 1, '小吃炸物', 3, 1),
  (4, 1, '饮品甜品', 4, 1);

INSERT INTO dish (id, name, category_id, price, image, description, status) VALUES
  (1, '宫保鸡丁饭', 2, 28.00, '/images/dish/kungpao.svg', '鸡丁鲜嫩，酸甜微辣，配米饭。', 1),
  (2, '黑椒牛柳饭', 2, 32.00, '/images/dish/beef.svg', '牛柳现炒，黑椒香气浓郁。', 1),
  (3, '番茄鸡蛋面', 2, 22.00, '/images/dish/noodle.svg', '汤底清爽，适合作为轻食主餐。', 1),
  (4, '黄金鸡翅', 3, 18.00, '/images/dish/wings.svg', '外皮酥脆，内里多汁。', 1),
  (5, '香脆薯条', 3, 12.00, '/images/dish/fries.svg', '经典小食，适合搭配饮品。', 1),
  (6, '柠檬红茶', 4, 9.00, '/images/dish/tea.svg', '冷泡红茶搭配鲜柠檬。', 1),
  (7, '芒果布丁', 4, 11.00, '/images/dish/pudding.svg', '口感细腻，芒果味浓。', 1);

INSERT INTO dish_flavor (dish_id, name, `value`) VALUES
  (1, '辣度', '不辣,微辣,中辣'),
  (1, '米饭', '正常,加饭'),
  (2, '口味', '标准,少黑椒'),
  (3, '面量', '正常,加面'),
  (4, '份量', '4只,6只'),
  (6, '温度', '少冰,正常冰,热饮');

INSERT INTO setmeal (id, category_id, name, price, image, description, status) VALUES
  (1, 1, '单人工作餐', 39.00, '/images/dish/combo-one.svg', '主食、炸物、饮品的单人组合。', 1),
  (2, 1, '双人分享餐', 69.00, '/images/dish/combo-two.svg', '两份主食加小吃饮品，适合双人。', 1);

INSERT INTO setmeal_dish (setmeal_id, dish_id, name, price, copies) VALUES
  (1, 1, '宫保鸡丁饭', 28.00, 1),
  (1, 5, '香脆薯条', 12.00, 1),
  (1, 6, '柠檬红茶', 9.00, 1),
  (2, 1, '宫保鸡丁饭', 28.00, 1),
  (2, 2, '黑椒牛柳饭', 32.00, 1),
  (2, 4, '黄金鸡翅', 18.00, 1),
  (2, 6, '柠檬红茶', 9.00, 2);
