package cn.bugstack.domain.order.service;

import cn.bugstack.domain.order.adapter.port.IProductPort;
import cn.bugstack.domain.order.adapter.repository.IOrderRepository;
import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ProductEntity;
import cn.bugstack.domain.order.model.entity.ShopCartEntity;
import cn.bugstack.domain.order.model.valobj.OrderStatusVO;
import com.alipay.api.AlipayApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
@Slf4j
@Service
public abstract class AbstractOrderService implements IOrderService {
    protected final IOrderRepository repository;
    protected final IProductPort port;

    public AbstractOrderService(IOrderRepository orderRepository, IProductPort port) {
        this.repository = orderRepository;
        this.port = port;
    }

    @Override
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws Exception {
        OrderEntity unpaidOrderEntity = repository.queryUnPayOrder(shopCartEntity);

        if (null != unpaidOrderEntity && OrderStatusVO.PAY_WAIT.equals(unpaidOrderEntity.getOrderStatusVO())) {
            log.info("创建订单-存在，已存在未支付订单。userId:{} productId:{} orderId:{}", shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpaidOrderEntity.getOrderId());
            return PayOrderEntity.builder()
                    .orderId(unpaidOrderEntity.getOrderId())
                    .payUrl(unpaidOrderEntity.getPayUrl())
                    .build();
        } else if (null != unpaidOrderEntity && OrderStatusVO.CREATE.equals(unpaidOrderEntity.getOrderStatusVO())) {
            log.info("创建订单-存在，存在未创建支付单订单，创建支付单开始 userId:{} productId:{} orderId:{}", shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpaidOrderEntity.getOrderId());
            PayOrderEntity payOrder = doPrepayOrder(shopCartEntity.getUserId(),shopCartEntity.getProductId(), unpaidOrderEntity.getProductName(), unpaidOrderEntity.getOrderId(),unpaidOrderEntity.getTotalAmount());
            return PayOrderEntity.builder()
                    .orderId(payOrder.getOrderId())
                    .payUrl(payOrder.getPayUrl())
                    .build();
        }

        ProductEntity productEntity = port.queryProductByProductId(shopCartEntity.getProductId());

        OrderEntity orderEntity = CreateOrderAggregate.buildOrderEntity(productEntity.getProductId(), productEntity.getProductName());

        CreateOrderAggregate orderAggregate = CreateOrderAggregate.builder()
                .userId(shopCartEntity.getUserId())
                .productEntity(productEntity)
                .orderEntity(orderEntity)
                .build();

        this.doSaveOrder(orderAggregate);
        PayOrderEntity payOrder = doPrepayOrder(shopCartEntity.getUserId(),productEntity.getProductId(),productEntity.getProductName(),orderEntity.getOrderId(),productEntity.getPrice());
        return PayOrderEntity.builder()
                .orderId(orderEntity.getOrderId())
                .payUrl("暂无")
                .build();

    }

    protected abstract PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException;
    protected abstract void doSaveOrder(CreateOrderAggregate orderAggregate);
    @Override
    public void changeOrderPaySuccess(String orderId) {
        repository.changeOrderPaySuccess(orderId);
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return repository.queryNoPayNotifyOrder();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return  repository.changeOrderClose(orderId);
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return repository.queryTimeoutCloseOrderList();
    }
}
