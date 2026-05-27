package com.sky.controller.admin;

import com.sky.config.AdminProperties;
import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AdminProperties adminProperties;

    @GetMapping("/list")
    public Result<List<OrderVO>> list(@RequestHeader(value = "X-Admin-Token", required = false) String token) {
        checkToken(token);
        return Result.success(orderService.adminList());
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id,
                                  @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        checkToken(token);
        return Result.success(orderService.adminDetail(id));
    }

    @PostMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id,
                                       @RequestParam Integer status,
                                       @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        checkToken(token);
        orderService.adminUpdateStatus(id, status);
        return Result.success();
    }

    private void checkToken(String token) {
        String adminToken = adminProperties.getToken();
        if (adminToken == null || adminToken.isBlank() || !adminToken.equals(token)) {
            throw new BaseException(MessageConstant.ADMIN_TOKEN_ERROR);
        }
    }
}
