package cn.bugstack.domain.order.service;

import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ShopCartEntity;
import com.alipay.api.AlipayApiException;

import java.util.Date;
import java.util.List;

public interface IOrderService {
    PayOrderEntity createOrder(ShopCartEntity shopCartReq) throws Exception;

    void changeOrderPaySuccess(String orderId, Date orderTime);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    void changeOrderMarketSettlement(List<String> outTradeNoList);

    List<OrderEntity> queryUserOrderList(String userId, Long lastId, int i);

    boolean refundMarketOrder(String userId, String orderId);

    boolean refundPayOrder(String userId, String outTradeNo) throws AlipayApiException;
}
