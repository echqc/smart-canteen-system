package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    @Scheduled(cron="0 * * * * ?")
    public void processTimeOutOrder() {
        log.info("开始处理超时订单{}", LocalDateTime.now());
        LocalDateTime time =LocalDateTime.now().plusMinutes(-15);
       List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT,time);
       if(orders!=null && orders.size()>0){
           orders.forEach(order -> {
               order.setStatus(Orders.CANCELLED);
               order.setCancelReason("超时未支付");
               order.setCancelTime(LocalDateTime.now());
               orderMapper.update(order);
           });
       }
    }
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveredOrder(){
        log.info("开始处理已配送订单{}", LocalDateTime.now());
        LocalDateTime time =LocalDateTime.now().minusMinutes(60);
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS,time);
        if(orders!=null && orders.size()>0){
            orders.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            });
        }

    }
}
