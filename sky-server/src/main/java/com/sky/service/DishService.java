package com.sky.service;

import com.sky.entity.Dish;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    public List<DishVO> list(Long categoryId) {
        List<Dish> dishes = dishMapper.listByCategoryId(categoryId);
        List<DishVO> result = new ArrayList<>();
        for (Dish dish : dishes) {
            DishVO vo = new DishVO();
            vo.id = dish.id;
            vo.name = dish.name;
            vo.categoryId = dish.categoryId;
            vo.price = dish.price;
            vo.image = dish.image;
            vo.description = dish.description;
            vo.status = dish.status;
            vo.flavors = dishFlavorMapper.listByDishId(dish.id);
            result.add(vo);
        }
        return result;
    }
}
