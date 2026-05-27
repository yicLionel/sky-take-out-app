INSERT INTO category (id, type, name, sort, status) VALUES
  (1, 1, '主食', 1, 1),
  (2, 1, '炒菜', 2, 1),
  (3, 1, '汤', 3, 1);

INSERT INTO dish (id, name, category_id, price, image, description, status) VALUES
  (1, '打抛饭', 1, 28.00, 'https://commons.wikimedia.org/wiki/Special:FilePath/Basil-fried_minced_pork_and_rice.jpg?width=640', '罗勒肉末香气浓郁，配米饭。', 1),
  (2, '卷饼', 1, 18.00, 'https://commons.wikimedia.org/wiki/Special:FilePath/Burrito.JPG?width=640', '薄饼卷入肉菜，方便饱腹。', 1),
  (3, '盖浇饭', 1, 24.00, 'https://commons.wikimedia.org/wiki/Special:FilePath/Gyudon-withegg-top.jpg?width=640', '热菜盖饭，酱汁拌饭。', 1),
  (4, '手撕包菜', 2, 18.00, 'https://commons.wikimedia.org/wiki/Special:FilePath/Stir_fry.jpg?width=640', '锅气足，爽脆微辣。', 1),
  (5, '茄子肉末', 2, 24.00, 'https://commons.wikimedia.org/wiki/Special:FilePath/Eggplant-sliced.jpg?width=640', '软糯茄子配肉末，下饭。', 1),
  (6, '番茄炒蛋', 2, 20.00, 'https://commons.wikimedia.org/wiki/Special:FilePath/Tomato_with_egg.jpg?width=640', '酸甜番茄和嫩滑鸡蛋。', 1),
  (7, '芹菜炒肉', 2, 24.00, 'https://commons.wikimedia.org/wiki/Special:FilePath/Chicken_and_Vegetable_Stir-Fry_(15914100572).jpg?width=640', '芹菜清香，肉片鲜嫩。', 1),
  (8, '麻婆豆腐', 2, 22.00, 'https://commons.wikimedia.org/wiki/Special:FilePath/Authentic_Mapo_Tofu.jpg?width=640', '麻辣鲜香，豆腐嫩滑。', 1),
  (9, '猪脚汤', 3, 32.00, 'https://commons.wikimedia.org/wiki/Special:FilePath/Bowl_of_Korean_short_rib_stew.jpg?width=640', '汤头醇厚，猪脚软糯。', 1),
  (10, '排骨汤', 3, 30.00, 'https://commons.wikimedia.org/wiki/Special:FilePath/Galbi-tang.jpg?width=640', '排骨慢炖，清香温润。', 1),
  (11, '番茄蛋汤', 3, 16.00, 'https://commons.wikimedia.org/wiki/Special:FilePath/Tomato_and_egg_soup.jpg?width=640', '清爽酸甜，适合搭配主食。', 1);

INSERT INTO dish_flavor (dish_id, name, `value`) VALUES
  (1, '辣度', '不辣,微辣,中辣'),
  (3, '米饭', '正常,加饭'),
  (4, '辣度', '不辣,微辣,中辣'),
  (8, '辣度', '微辣,中辣,特辣'),
  (9, '份量', '小份,大份'),
  (10, '份量', '小份,大份');
