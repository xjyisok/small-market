package cn.bugstack.infrastructure.dao;


import cn.bugstack.infrastructure.dao.po.PayOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IOrderDao {

    void insert(PayOrder payOrder);

    PayOrder queryUnPayOrder(PayOrder payOrder);

    void updateOrderPayInfo(PayOrder payOrder);

    void changeOrderPaySuccess(PayOrder order);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    PayOrder queryOrderByOrderId(String orderId);

    void changeOrderMarketSettlement(List<String> outTradeNoList);

    void changeOrderDealDone(String tradeNo);

//    List<PayOrder> queryUserOrderList(String userId, Long lastId, int pageSize);
//
//    PayOrder queryOrderByUserIdAndOrderId(String userId, String orderId);
//
//    boolean refundOrder(String userId, String orderId);
//
//    boolean refundMarketOrder(String userId, String orderId);
List<PayOrder> queryUserOrderList(
        @Param("userId") String userId,
        @Param("lastId") Long lastId,
        @Param("pageSize") int pageSize
);

    PayOrder queryOrderByUserIdAndOrderId(
            @Param("userId") String userId,
            @Param("orderId") String orderId
    );

    boolean refundOrder(
            @Param("userId") String userId,
            @Param("orderId") String orderId
    );

    boolean refundMarketOrder(
            @Param("userId") String userId,
            @Param("orderId") String orderId
    );

}
