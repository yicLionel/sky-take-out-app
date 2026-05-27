package com.sky.service;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BaseException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class OrderService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO dto) {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getByIdAndUserId(dto.addressBookId, userId);
        if (addressBook == null) {
            throw new BaseException(MessageConstant.ADDRESS_NOT_FOUND);
        }
        List<ShoppingCart> cartList = shoppingCartMapper.list(userId);
        if (cartList.isEmpty()) {
            throw new BaseException(MessageConstant.SHOPPING_CART_EMPTY);
        }

        BigDecimal amount = BigDecimal.ZERO;
        for (ShoppingCart cart : cartList) {
            amount = amount.add(cart.amount.multiply(BigDecimal.valueOf(cart.number)));
        }

        LocalDateTime now = LocalDateTime.now();
        Orders orders = new Orders();
        orders.number = buildOrderNumber(now);
        orders.status = Orders.PENDING_PAYMENT;
        orders.userId = userId;
        orders.addressBookId = addressBook.id;
        orders.orderTime = now;
        orders.payStatus = Orders.UN_PAID;
        orders.amount = amount;
        orders.remark = dto.remark;
        orders.phone = addressBook.phone;
        orders.address = joinAddress(addressBook);
        orders.consignee = addressBook.consignee;
        orders.packAmount = BigDecimal.ZERO;
        orders.tablewareNumber = dto.tablewareNumber;
        orders.tablewareStatus = dto.tablewareStatus;
        orderMapper.insert(orders);

        for (ShoppingCart cart : cartList) {
            OrderDetail detail = new OrderDetail();
            detail.name = cart.name;
            detail.image = cart.image;
            detail.orderId = orders.id;
            detail.dishId = cart.dishId;
            detail.setmealId = cart.setmealId;
            detail.dishFlavor = cart.dishFlavor;
            detail.number = cart.number;
            detail.amount = cart.amount;
            orderDetailMapper.insert(detail);
        }
        shoppingCartMapper.clean(userId);

        OrderSubmitVO vo = new OrderSubmitVO();
        vo.id = orders.id;
        vo.orderNumber = orders.number;
        vo.orderAmount = orders.amount;
        vo.orderTime = orders.orderTime;
        return vo;
    }

    @Transactional
    public OrderPaymentVO payment(OrdersPaymentDTO dto) {
        Long userId = BaseContext.getCurrentId();
        Orders orders = orderMapper.getByIdAndUserId(dto.orderId, userId);
        if (orders == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (orders.status != Orders.PENDING_PAYMENT) {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }
        orders.status = Orders.PAID;
        orders.payStatus = Orders.PAY_SUCCESS;
        orders.checkoutTime = LocalDateTime.now();
        orderMapper.updatePayment(orders);

        OrderPaymentVO vo = new OrderPaymentVO();
        vo.orderId = orders.id;
        vo.status = orders.status;
        vo.message = "支付成功";
        return vo;
    }

    public List<OrderVO> history() {
        Long userId = BaseContext.getCurrentId();
        List<Orders> ordersList = orderMapper.listByUserId(userId);
        List<OrderVO> result = new ArrayList<>();
        for (Orders orders : ordersList) {
            result.add(toVO(orders));
        }
        return result;
    }

    public OrderVO detail(Long id) {
        Orders orders = orderMapper.getByIdAndUserId(id, BaseContext.getCurrentId());
        if (orders == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        return toVO(orders);
    }

    public List<OrderVO> adminList() {
        List<Orders> ordersList = orderMapper.listAll();
        List<OrderVO> result = new ArrayList<>();
        for (Orders orders : ordersList) {
            result.add(toVO(orders));
        }
        return result;
    }

    public OrderVO adminDetail(Long id) {
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        return toVO(orders);
    }

    public void adminUpdateStatus(Long id, Integer status) {
        if (status == null || status < Orders.PENDING_PAYMENT || status > Orders.CANCELLED) {
            throw new BaseException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = orderMapper.getById(id);
        if (orders == null) {
            throw new BaseException(MessageConstant.ORDER_NOT_FOUND);
        }
        orderMapper.updateStatus(id, status);
    }

    private OrderVO toVO(Orders orders) {
        OrderVO vo = new OrderVO();
        vo.id = orders.id;
        vo.number = orders.number;
        vo.status = orders.status;
        vo.userId = orders.userId;
        vo.orderTime = orders.orderTime;
        vo.checkoutTime = orders.checkoutTime;
        vo.payStatus = orders.payStatus;
        vo.amount = orders.amount;
        vo.remark = orders.remark;
        vo.phone = orders.phone;
        vo.address = orders.address;
        vo.consignee = orders.consignee;
        vo.orderDetails = orderDetailMapper.listByOrderId(orders.id);
        return vo;
    }

    private String buildOrderNumber(LocalDateTime now) {
        String time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(9000) + 1000;
        return time + random;
    }

    private String joinAddress(AddressBook addressBook) {
        String province = addressBook.provinceName == null ? "" : addressBook.provinceName;
        String city = addressBook.cityName == null ? "" : addressBook.cityName;
        String district = addressBook.districtName == null ? "" : addressBook.districtName;
        return province + city + district + addressBook.detail;
    }
}
