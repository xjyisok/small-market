package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.order.adapter.event.PaySuccessMesageEvenet;
import cn.bugstack.domain.order.adapter.repository.IOrderRepository;
import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ProductEntity;
import cn.bugstack.domain.order.model.entity.ShopCartEntity;
import cn.bugstack.domain.order.model.valobj.OrderStatusVO;
import cn.bugstack.infrastructure.dao.IOrderDao;
import cn.bugstack.infrastructure.dao.po.PayOrder;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.event.BaseEvent;
import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Repository
public class OrderRepositoryImpl implements IOrderRepository {
    @Resource
    private IOrderDao orderDao;
    @Resource
    BaseEvent<PaySuccessMesageEvenet.PaySuccessMessage> paySuccessEvent;
    @Resource
    EventBus eventBus;
    @Override
    public OrderEntity queryUnPayOrder(ShopCartEntity shopCartEntity) {
        PayOrder payOrderreq = new PayOrder();
        payOrderreq.setUserId(shopCartEntity.getUserId());
        payOrderreq.setProductId(shopCartEntity.getProductId());
        PayOrder payOrder = orderDao.queryUnPayOrder(payOrderreq);

        if (payOrder== null) {
            return null;
        }
       return OrderEntity.builder()
               .productId(payOrder.getProductId())
               .orderId(payOrder.getOrderId())
               .productName(payOrder.getProductName())
               .orderStatusVO(OrderStatusVO.valueOf(payOrder.getStatus()))
               .orderTime(payOrder.getOrderTime())
               .totalAmount(payOrder.getTotalAmount())
               .payUrl(payOrder.getPayUrl())
               .build();
    }

    @Override
    public void doSaveOrder(CreateOrderAggregate orderAggregate) {
        String userId = orderAggregate.getUserId();
        ProductEntity productEntity = orderAggregate.getProductEntity();
        OrderEntity orderEntity=orderAggregate.getOrderEntity();
        PayOrder payOrder = new PayOrder();
        payOrder.setUserId(userId);
        payOrder.setProductId(productEntity.getProductId());
        payOrder.setProductName(productEntity.getProductName());
        payOrder.setOrderId(orderEntity.getOrderId());
        payOrder.setOrderTime(orderEntity.getOrderTime());
        payOrder.setTotalAmount(orderEntity.getTotalAmount());
        payOrder.setPayUrl(orderEntity.getPayUrl());
        payOrder.setStatus(orderEntity.getOrderStatusVO().getCode());
        orderDao.insert(payOrder);
    }

    @Override
    public void updateOrderPayInfo(PayOrderEntity payOrder) {
        PayOrder payOrderreq = new PayOrder();
        payOrderreq.setUserId(payOrder.getUserId());
        payOrderreq.setPayUrl(payOrder.getPayUrl());
        payOrderreq.setOrderId(payOrder.getOrderId());
        payOrderreq.setStatus(payOrder.getOrderStatus().getCode());
        orderDao.updateOrderPayInfo(payOrderreq);
    }

    @Override
    public void changeOrderPaySuccess(String orderId) {PayOrder payOrderReq = new PayOrder();
        payOrderReq.setOrderId(orderId);
        payOrderReq.setStatus(OrderStatusVO.PAY_SUCCESS.getCode());
        orderDao.changeOrderPaySuccess(payOrderReq);
        BaseEvent.EventMessage<PaySuccessMesageEvenet.PaySuccessMessage>payEventMessage=paySuccessEvent.buildEventMessage(PaySuccessMesageEvenet.PaySuccessMessage.builder()
                        .userId(payOrderReq.getUserId())
                        .tradeNo(orderId)
                .build());
        PaySuccessMesageEvenet.PaySuccessMessage paySuccessMessage=payEventMessage.getData();
        eventBus.post(paySuccessMessage);
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return orderDao.queryNoPayNotifyOrder();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return orderDao.changeOrderClose(orderId);
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return orderDao.queryTimeoutCloseOrderList();
    }
}
