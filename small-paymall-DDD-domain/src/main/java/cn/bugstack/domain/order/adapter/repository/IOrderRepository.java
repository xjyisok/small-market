package cn.bugstack.domain.order.adapter.repository;

import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ShopCartEntity;

import java.util.Date;
import java.util.List;

public interface IOrderRepository {
    OrderEntity queryUnPayOrder(ShopCartEntity shopCartEntity);

    void doSaveOrder(CreateOrderAggregate orderAggregate);

    void updateOrderPayInfo(PayOrderEntity payOrder);

    void changeOrderPaySuccess(String orderId, Date orderTime);

    List<String> queryNoPayNotifyOrder();

    boolean changeOrderClose(String orderId);

    List<String> queryTimeoutCloseOrderList();

    OrderEntity queryOrderByOrderId(String orderId);

    void changeMarketOrderPaySuccess(String orderId);

    void changeOrderMarketSettlement(List<String> outTradeNoList);

    List<OrderEntity> queryUserOrderList(String userId, Long lastId, int pageSize);

    OrderEntity queryOrderByUserIdAndOrderId(String userId, String orderId);

    boolean refundOrder(String userId, String orderId);

    boolean refundMarketOrder(String userId, String orderId);
}
