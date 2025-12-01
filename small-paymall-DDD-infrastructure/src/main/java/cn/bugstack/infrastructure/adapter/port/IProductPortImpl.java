package cn.bugstack.infrastructure.adapter.port;

import cn.bugstack.domain.order.adapter.port.IProductPort;
import cn.bugstack.domain.order.model.entity.MarketPayDiscountEntity;
import cn.bugstack.domain.order.model.entity.ProductEntity;
import cn.bugstack.infrastructure.gateway.IGroupBuyMarketService;
import cn.bugstack.infrastructure.gateway.ProductRPC;
import cn.bugstack.infrastructure.gateway.dto.LockMarketPayOrderRequestDTO;
import cn.bugstack.infrastructure.gateway.dto.LockMarketPayOrderResponseDTO;
import cn.bugstack.infrastructure.gateway.dto.ProductDTO;
import cn.bugstack.infrastructure.gateway.response.Response;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

@Service
@Slf4j
public class IProductPortImpl implements IProductPort {
    @Value("${app.config.group-buy-market.source}")
    private String source;
    @Value("${app.config.group-buy-market.chanel}")
    private String chanel;
    @Value("${app.config.group-buy-market.notify-url}")
    private String notifyUrl;

    private final ProductRPC productRPC;
    private final IGroupBuyMarketService groupBuyMarketService;

    public IProductPortImpl(ProductRPC productRPC, IGroupBuyMarketService groupBuyMarketService) {
        this.productRPC = productRPC;
        this.groupBuyMarketService = groupBuyMarketService;
    }

    @Override
    public ProductEntity queryProductByProductId(String productId) {
        ProductDTO productDTO = productRPC.queryProductByProductId(productId);
        return ProductEntity.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getProductName())
                .productDesc(productDTO.getProductDesc())
                .price(productDTO.getPrice())
                .build();
    }

    @Override
    public MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId) {
        System.out.println("开始锁单了！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
        // 请求参数
        LockMarketPayOrderRequestDTO requestDTO = new LockMarketPayOrderRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setTeamId(teamId);
        requestDTO.setGoodsId(productId);
        requestDTO.setActivityId(activityId);
        requestDTO.setSource(source);
        requestDTO.setChannel(chanel);
        requestDTO.setOutTradeNo(orderId);
        requestDTO.setNotifyUrl(notifyUrl);

        try {
            // 营销锁单
            Call<Response<LockMarketPayOrderResponseDTO>> call = groupBuyMarketService.lockMarketPayOrder(requestDTO);

            // 获取结果
            Response<LockMarketPayOrderResponseDTO> response = call.execute().body();
            log.info("营销锁单{} requestDTO:{} responseDTO:{}", userId, JSON.toJSONString(requestDTO), JSON.toJSONString(response));
            if (null == response) return null;

            // 异常判断
            if (!"0000".equals(response.getCode())) {
                throw new AppException(response.getCode(), response.getInfo());
            }

            LockMarketPayOrderResponseDTO responseDTO = response.getData();

            // 获取拼团优惠
            return MarketPayDiscountEntity.builder()
                    .originalPrice(responseDTO.getOriginalPrice())
                    .deductionPrice(responseDTO.getDeductionPrice())
                    .payPrice(responseDTO.getPayPrice())
                    .build();
        } catch (Exception e) {
            log.error("营销锁单失败{}", userId, e);
            return null;
        }

    }
}
