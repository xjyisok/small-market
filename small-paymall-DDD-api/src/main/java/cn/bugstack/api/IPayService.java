package cn.bugstack.api;

import cn.bugstack.api.dto.*;
import cn.bugstack.api.response.Response;

public interface IPayService {
    public Response<String> createPayOrder(CreatePayRequestDTO createPayRequestDTO);
    String groupBuyNotify(NotifyRequestDTO notifyRequestDTO);
    /**
     * 查询用户订单列表
     *
     * @param requestDTO 请求对象
     * @return 订单列表
     */
    Response<QueryOrderListResponseDTO> queryUserOrderList(QueryOrderListRequestDTO requestDTO);

    /**
     * 用户退单
     *
     * @param requestDTO 请求对象
     * @return 退单结果
     */
    Response<RefundOrderResponseDTO> refundOrder(RefundOrderRequestDTO requestDTO);

}
