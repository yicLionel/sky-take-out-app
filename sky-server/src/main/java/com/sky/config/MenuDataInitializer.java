package com.sky.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class MenuDataInitializer implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    public MenuDataInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!hasCategoryTable()) {
            return;
        }

        jdbcTemplate.update("delete from shopping_cart");
        jdbcTemplate.update("delete from setmeal_dish");
        jdbcTemplate.update("delete from setmeal");
        jdbcTemplate.update("delete from dish_flavor");
        jdbcTemplate.update("delete from dish");
        jdbcTemplate.update("delete from category");

        insertCategories();
        insertDishes();
        insertFlavors();
    }

    private boolean hasCategoryTable() {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*)
                from information_schema.tables
                where table_schema = database()
                  and table_name = 'category'
                """, Integer.class);
        return count != null && count > 0;
    }

    private void insertCategories() {
        jdbcTemplate.update("insert into category (id, type, name, sort, status) values (1, 1, '主食', 1, 1)");
        jdbcTemplate.update("insert into category (id, type, name, sort, status) values (2, 1, '炒菜', 2, 1)");
        jdbcTemplate.update("insert into category (id, type, name, sort, status) values (3, 1, '汤', 3, 1)");
    }

    private void insertDishes() {
        insertDish(1, "打抛饭", 1, "28.00", "/images/dish/basil-rice.svg", "罗勒肉末香气浓郁，配米饭。");
        insertDish(2, "卷饼", 1, "18.00", "/images/dish/wrap.svg", "薄饼卷入肉菜，方便饱腹。");
        insertDish(3, "盖浇饭", 1, "24.00", "/images/dish/rice-bowl.svg", "热菜盖饭，酱汁拌饭。");
        insertDish(4, "手撕包菜", 2, "18.00", "/images/dish/cabbage.svg", "锅气足，爽脆微辣。");
        insertDish(5, "茄子肉末", 2, "24.00", "/images/dish/eggplant-pork.svg", "软糯茄子配肉末，下饭。");
        insertDish(6, "番茄炒蛋", 2, "20.00", "/images/dish/tomato-egg.svg", "酸甜番茄和嫩滑鸡蛋。");
        insertDish(7, "芹菜炒肉", 2, "24.00", "/images/dish/celery-pork.svg", "芹菜清香，肉片鲜嫩。");
        insertDish(8, "麻婆豆腐", 2, "22.00", "/images/dish/mapo-tofu.svg", "麻辣鲜香，豆腐嫩滑。");
        insertDish(9, "猪脚汤", 3, "32.00", "/images/dish/pork-feet-soup.svg", "汤头醇厚，猪脚软糯。");
        insertDish(10, "排骨汤", 3, "30.00", "/images/dish/rib-soup.svg", "排骨慢炖，清香温润。");
        insertDish(11, "番茄蛋汤", 3, "16.00", "/images/dish/tomato-egg-soup.svg", "清爽酸甜，适合搭配主食。");
    }

    private void insertDish(int id, String name, int categoryId, String price, String image, String description) {
        jdbcTemplate.update("""
                insert into dish (id, name, category_id, price, image, description, status)
                values (?, ?, ?, ?, ?, ?, 1)
                """, id, name, categoryId, price, image, description);
    }

    private void insertFlavors() {
        jdbcTemplate.update("insert into dish_flavor (dish_id, name, `value`) values (1, '辣度', '不辣,微辣,中辣')");
        jdbcTemplate.update("insert into dish_flavor (dish_id, name, `value`) values (3, '米饭', '正常,加饭')");
        jdbcTemplate.update("insert into dish_flavor (dish_id, name, `value`) values (4, '辣度', '不辣,微辣,中辣')");
        jdbcTemplate.update("insert into dish_flavor (dish_id, name, `value`) values (8, '辣度', '微辣,中辣,特辣')");
        jdbcTemplate.update("insert into dish_flavor (dish_id, name, `value`) values (9, '份量', '小份,大份')");
        jdbcTemplate.update("insert into dish_flavor (dish_id, name, `value`) values (10, '份量', '小份,大份')");
    }
}
