package com.sky;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
class SkyMvpApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void userOrderFlowWorks() throws Exception {
        String loginJson = mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"mvp-test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();
        String token = JsonPath.read(loginJson, "$.data.token");
        assertThat(token).isNotBlank();

        String categoryJson = mockMvc.perform(get("/user/category/list").header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<?> categories = JsonPath.read(categoryJson, "$.data");
        assertThat(categories).isNotEmpty();

        String dishJson = mockMvc.perform(get("/user/dish/list?categoryId=2").header("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<?> dishes = JsonPath.read(dishJson, "$.data");
        assertThat(dishes).isNotEmpty();

        mockMvc.perform(post("/user/shoppingCart/add")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dishId\":1,\"dishFlavor\":\"辣度:微辣\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));

        String cartJson = mockMvc.perform(get("/user/shoppingCart/list").header("token", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<?> cartList = JsonPath.read(cartJson, "$.data");
        assertThat(cartList).hasSize(1);

        String addressJson = mockMvc.perform(post("/user/addressBook")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "consignee": "测试用户",
                                  "sex": "先生",
                                  "phone": "13800000000",
                                  "provinceName": "上海市",
                                  "cityName": "上海市",
                                  "districtName": "浦东新区",
                                  "detail": "张江路100号",
                                  "defaultStatus": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Integer addressId = JsonPath.read(addressJson, "$.data.id");

        String submitJson = mockMvc.perform(post("/user/order/submit")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "addressBookId": %d,
                                  "remark": "少放葱",
                                  "tablewareNumber": 1,
                                  "tablewareStatus": 1
                                }
                                """.formatted(addressId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Integer orderId = JsonPath.read(submitJson, "$.data.id");
        Double orderAmount = JsonPath.read(submitJson, "$.data.orderAmount");
        assertThat(BigDecimal.valueOf(orderAmount)).isEqualByComparingTo("28.0");

        mockMvc.perform(post("/user/order/payment")
                        .header("token", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":" + orderId + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(2));

        String historyJson = mockMvc.perform(get("/user/order/historyOrders").header("token", token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<?> orders = JsonPath.read(historyJson, "$.data");
        assertThat(orders).hasSize(1);
    }
}
