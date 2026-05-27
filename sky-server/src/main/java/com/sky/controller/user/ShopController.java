package com.sky.controller.user;

import com.sky.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shop")
public class ShopController {
    @GetMapping("/status")
    public Result<Integer> status() {
        return Result.success(1);
    }
}
