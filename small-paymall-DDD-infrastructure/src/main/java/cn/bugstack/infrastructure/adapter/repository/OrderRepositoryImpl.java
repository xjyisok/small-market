package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.order.adapter.event.PaySuccessMesageEvenet;
import cn.bugstack.domain.order.adapter.repository.IOrderRepository;
import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ProductEntity;
import cn.bugstack.domain.order.model.entity.ShopCartEntity;
import cn.bugstack.domain.order.model.valobj.MarketTypeVO;
import cn.bugstack.domain.order.model.valobj.OrderStatusVO;
import cn.bugstack.infrastructure.dao.IOrderDao;
import cn.bugstack.infrastructure.dao.po.PayOrder;
import cn.bugstack.infrastructure.event.EventPublisher;
import cn.bugstack.types.common.Constants;
import cn.bugstack.types.event.BaseEvent;
import com.alibaba.fastjson2.JSON;
import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryImpl implements IOrderRepository {
    @Resource
    private IOrderDao orderDao;
    @Resource
    BaseEvent<PaySuccessMesageEvenet.PaySuccessMessage> paySuccessEvent;
    @Resource
    EventBus eventBus;
    @Resource
    EventPublisher eventPublisher;
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
               .marketType(payOrder.getMarketType())
               .payAmount(payOrder.getPayAmount())
               .marketDeductionAmount(payOrder.getMarketDeductionAmount())
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
        payOrder.setTotalAmount(productEntity.getPrice());
        payOrder.setPayUrl(orderEntity.getPayUrl());
        payOrder.setStatus(orderEntity.getOrderStatusVO().getCode());
        payOrder.setMarketType(orderEntity.getMarketType());
        payOrder.setMarketDeductionAmount(BigDecimal.ZERO);
        payOrder.setPayAmount(productEntity.getPrice());

        orderDao.insert(payOrder);
    }

    @Override
    public void updateOrderPayInfo(PayOrderEntity payOrder) {
        PayOrder payOrderreq = new PayOrder();
        payOrderreq.setUserId(payOrder.getUserId());
        payOrderreq.setPayUrl(payOrder.getPayUrl());
        payOrderreq.setOrderId(payOrder.getOrderId());
        payOrderreq.setStatus(payOrder.getOrderStatus().getCode());
        payOrderreq.setMarketType(payOrder.getMarketType());
        payOrderreq.setMarketDeductionAmount(payOrder.getMarketDeductionAmount());
        payOrderreq.setPayAmount(payOrder.getPayAmount());
        orderDao.updateOrderPayInfo(payOrderreq);
    }

    @Override
    public void changeOrderPaySuccess(String orderId, Date orderTime) {
        System.out.println("结算NO-GROUP_BUY_MARKET！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
        PayOrder payOrderReq = new PayOrder();
        payOrderReq.setOrderId(orderId);
        payOrderReq.setStatus(OrderStatusVO.PAY_SUCCESS.getCode());
        orderDao.changeOrderPaySuccess(payOrderReq);
        BaseEvent.EventMessage<PaySuccessMesageEvenet.PaySuccessMessage>payEventMessage=paySuccessEvent.buildEventMessage(PaySuccessMesageEvenet.PaySuccessMessage.builder()
                        .userId(payOrderReq.getUserId())
                        .tradeNo(orderId)
                .build());
        PaySuccessMesageEvenet.PaySuccessMessage paySuccessMessage=payEventMessage.getData();
        eventBus.post(JSON.toJSON(paySuccessMessage));
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

    @Override
    public OrderEntity queryOrderByOrderId(String orderId) {
        PayOrder payOrder=orderDao.queryOrderByOrderId(orderId);
        if(null==payOrder){
            return null;
        }
        OrderEntity orderEntity=OrderEntity.builder()
                .userId(payOrder.getUserId())
                .productId(payOrder.getProductId())
                .productName(payOrder.getProductName())
                .orderId(payOrder.getOrderId())
                .orderTime(payOrder.getOrderTime())
                .totalAmount(payOrder.getTotalAmount())
                .payUrl(payOrder.getPayUrl())
                .marketType(payOrder.getMarketType())
                .marketDeductionAmount(payOrder.getMarketDeductionAmount())
                .payAmount(payOrder.getPayAmount())
                .build();
        return orderEntity;
    }

    @Override
    public void changeMarketOrderPaySuccess(String orderId) {
        System.out.println("changeMarketOrderPaySuccess!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        PayOrder payOrderReq = new PayOrder();
        payOrderReq.setOrderId(orderId);
        payOrderReq.setStatus(OrderStatusVO.PAY_SUCCESS.getCode());
        orderDao.changeOrderPaySuccess(payOrderReq);
    }

    @Override
    public void changeOrderMarketSettlement(List<String> outTradeNoList) {
        // 更新拼团结算状态
        orderDao.changeOrderMarketSettlement(outTradeNoList);

        // 循环成功发送消息 - 一般在公司的场景里，还会有job任务扫描超时没有结算的订单，查询订单状态。查询对方服务端的接口，会被限制一次查询多少，频次多少。
        outTradeNoList.forEach(outTradeNo -> {
            BaseEvent.EventMessage<PaySuccessMesageEvenet.PaySuccessMessage> paySuccessMessageEventMessage = paySuccessEvent.buildEventMessage(
                    PaySuccessMesageEvenet.PaySuccessMessage.builder()
                            .tradeNo(outTradeNo)
                            .build());
            PaySuccessMesageEvenet.PaySuccessMessage paySuccessMessage = paySuccessMessageEventMessage.getData();

            //eventBus.post(JSON.toJSONString(paySuccessMessage));
            eventPublisher.publish(paySuccessEvent.topic(),JSON.toJSONString(paySuccessMessage));
        });
    }

    @Override
    public List<OrderEntity> queryUserOrderList(String userId, Long lastId, int pageSize) {
        List<PayOrder> payOrderList = orderDao.queryUserOrderList(userId, lastId, pageSize);
        if (null == payOrderList || payOrderList.isEmpty()) {
            return new ArrayList<>();
        }

        return payOrderList.stream().map(payOrder -> OrderEntity.builder()
                .id(payOrder.getId())
                .userId(payOrder.getUserId())
                .productId(payOrder.getProductId())
                .productName(payOrder.getProductName())
                .orderId(payOrder.getOrderId())
                .orderTime(payOrder.getOrderTime())
                .totalAmount(payOrder.getTotalAmount())
                .orderStatusVO(OrderStatusVO.valueOf(payOrder.getStatus()))
                .payUrl(payOrder.getPayUrl())
                .payTime(payOrder.getPayTime())
                .marketType(payOrder.getMarketType())
                .marketDeductionAmount(payOrder.getMarketDeductionAmount())
                .payAmount(payOrder.getPayAmount())
                .build()).collect(Collectors.toList());
    }

    @Override
    public OrderEntity queryOrderByUserIdAndOrderId(String userId, String orderId) {
        PayOrder payOrder = orderDao.queryOrderByUserIdAndOrderId(userId, orderId);
        if (null == payOrder) return null;

        return OrderEntity.builder()
                .id(payOrder.getId())
                .userId(payOrder.getUserId())
                .productId(payOrder.getProductId())
                .productName(payOrder.getProductName())
                .orderId(payOrder.getOrderId())
                .orderTime(payOrder.getOrderTime())
                .totalAmount(payOrder.getTotalAmount())
                .orderStatusVO(OrderStatusVO.valueOf(payOrder.getStatus()))
                .payUrl(payOrder.getPayUrl())
                .payTime(payOrder.getPayTime())
                .marketType(payOrder.getMarketType())
                .marketDeductionAmount(payOrder.getMarketDeductionAmount())
                .payAmount(payOrder.getPayAmount())
                .build();

    }

    @Override
    public boolean refundOrder(String userId, String orderId) {
        return orderDao.refundOrder(userId, orderId);
    }
}
