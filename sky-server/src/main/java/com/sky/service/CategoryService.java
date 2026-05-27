package com.sky.service;

import com.sky.constant.StatusConstant;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> list() {
        return categoryMapper.list(StatusConstant.ENABLE);
    }
}
