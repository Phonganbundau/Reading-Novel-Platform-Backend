package com.truyenchu.demo.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.truyenchu.demo.entity.Order;
import com.truyenchu.demo.entity.User;
import com.truyenchu.demo.repository.OrderRepository;
import com.truyenchu.demo.service.OrderService;
import com.truyenchu.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import vn.payos.PayOS;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
  private final PayOS payOS;
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

  
    @PostMapping(path = "/payos_transfer_handler")
    public ObjectNode payosTransferHandler(@RequestBody ObjectNode body)
        throws JsonProcessingException, IllegalArgumentException {
  
      ObjectNode response = objectMapper.createObjectNode();
      // Check if 'success' field exists and is not null
      if (!body.hasNonNull("success")) {
          response.put("error", -1);
          response.put("message", "Missing or null 'success' field in webhook body");
          response.set("data", null);
          return response;
      }
      Webhook webhookBody = objectMapper.treeToValue(body, Webhook.class);
  
      try {
        // Init Response
        response.put("error", 0);
        response.put("message", "Webhook delivered");
        response.set("data", null);
  
        WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);
        System.out.println(data);
  
        if (webhookBody.getSuccess() != null && webhookBody.getSuccess()) {
  
        Order order = orderService.getOrderByOrderCode(webhookBody.getData().getOrderCode());
         if (order != null) {

            if (order.getStatus() != Order.OrderStatus.PENDING) {
                return response;
            }

          order.setStatus(Order.OrderStatus.SUCCESS);
          User user = userService.findById(order.getUser().getId());
          user.setCoinBalance(user.getCoinBalance().add(BigDecimal.valueOf(order.getCoins())));
          userService.save(user);
          orderRepository.save(order);
         }
     }

     
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      response.put("error", -1);
      response.put("message", e.getMessage());
      response.set("data", null);
      return response;
    }
  }
}