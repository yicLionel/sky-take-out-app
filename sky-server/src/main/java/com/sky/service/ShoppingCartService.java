package com.sky.service;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BaseException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    public List<ShoppingCart> list() {
        return shoppingCartMapper.list(BaseContext.getCurrentId());
    }

    public void add(ShoppingCartDTO dto) {
        if (dto == null || (dto.dishId == null && dto.setmealId == null)) {
            throw new BaseException("SHOPPING_CART_ITEM_REQUIRED");
        }
        Long userId = BaseContext.getCurrentId();
        String dishFlavor = dto.dishFlavor == null ? "" : dto.dishFlavor;
        ShoppingCart old = dto.dishId != null
                ? shoppingCartMapper.getExistingDish(userId, dto.dishId, dishFlavor)
                : shoppingCartMapper.getExistingSetmeal(userId, dto.setmealId);
        if (old != null) {
            shoppingCartMapper.addNumber(old.id, userId);
            return;
        }
        ShoppingCart cart = new ShoppingCart();
        cart.userId = userId;
        cart.dishId = dto.dishId;
        cart.setmealId = dto.setmealId;
        cart.dishFlavor = dishFlavor;
        cart.number = 1;
        cart.createTime = LocalDateTime.now();
        if (dto.dishId != null) {
            Dish dish = dishMapper.getById(dto.dishId);
            if (dish == null) {
                throw new BaseException("DISH_NOT_FOUND");
            }
            cart.name = dish.name;
            cart.image = dish.image;
            cart.amount = dish.price;
        } else if (dto.setmealId != null) {
            Setmeal setmeal = setmealMapper.getById(dto.setmealId);
            if (setmeal == null) {
                throw new BaseException("SETMEAL_NOT_FOUND");
            }
            cart.name = setmeal.name;
            cart.image = setmeal.image;
            cart.amount = setmeal.price;
        } else {
            throw new BaseException("SHOPPING_CART_ITEM_REQUIRED");
        }
        shoppingCartMapper.insert(cart);
    }

    public void sub(ShoppingCartDTO dto) {
        if (dto == null || (dto.dishId == null && dto.setmealId == null)) {
            return;
        }
        Long userId = BaseContext.getCurrentId();
        String dishFlavor = dto.dishFlavor == null ? "" : dto.dishFlavor;
        ShoppingCart old = dto.dishId != null
                ? shoppingCartMapper.getExistingDish(userId, dto.dishId, dishFlavor)
                : shoppingCartMapper.getExistingSetmeal(userId, dto.setmealId);
        if (old == null) {
            return;
        }
        if (old.number != null && old.number > 1) {
            shoppingCartMapper.subNumber(old.id, userId);
        } else {
            shoppingCartMapper.deleteById(old.id, userId);
        }
    }

    public void clean() {
        shoppingCartMapper.clean(BaseContext.getCurrentId());
    }
}
