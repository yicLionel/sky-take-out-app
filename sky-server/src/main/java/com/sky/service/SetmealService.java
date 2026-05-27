package com.sky.service;

import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    public List<Setmeal> list(Long categoryId) {
        return setmealMapper.listByCategoryId(categoryId);
    }
}
