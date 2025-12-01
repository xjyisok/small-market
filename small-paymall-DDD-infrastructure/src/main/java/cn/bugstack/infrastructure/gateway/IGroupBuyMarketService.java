package cn.bugstack.infrastructure.gateway;

import cn.bugstack.infrastructure.gateway.dto.LockMarketPayOrderRequestDTO;
import cn.bugstack.infrastructure.gateway.dto.LockMarketPayOrderResponseDTO;
import cn.bugstack.infrastructure.gateway.dto.SettlementMarketPayOrderRequestDTO;
import cn.bugstack.infrastructure.gateway.dto.SettlementMarketPayOrderResponseDTO;
import cn.bugstack.infrastructure.gateway.response.Response;
import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.POST;
public interface IGroupBuyMarketService {
    @POST("/api/v1/gbm/trade/lock_market_pay_order")
    Call<Response<LockMarketPayOrderResponseDTO>> lockMarketPayOrder(@Body LockMarketPayOrderRequestDTO lockMarketPayOrderRequestDTO);
    @POST("/api/v1/gbm/trade/settlement_market_pay_order")
    Call<Response<SettlementMarketPayOrderResponseDTO>> settlementMarketPayOrder(@Body SettlementMarketPayOrderRequestDTO settlementMarketPayOrderRequestDTO);
}
