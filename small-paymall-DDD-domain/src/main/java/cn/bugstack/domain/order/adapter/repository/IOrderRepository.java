package cn.bugstack.domain.order.adapter.repository;

import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ShopCartEntity;

import java.util.List;

public interface IOrderRepository {
    OrderEntity queryUnPayOrder(ShopCartEntity shopCartEntity);

    void doSaveOrder(CreateOrderAggregate orderAggregate);

    void updateOrderPayInfo(PayOrderEntity payOrder);

    void changeOrderPaySuccess(String orderId);

    List<String> queryNoPayNotifyOrder();

    boolean changeOrderClose(String orderId);

    List<String> queryTimeoutCloseOrderList();
}
