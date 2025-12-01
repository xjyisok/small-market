package cn.bugstack.domain.order.model.entity;

import cn.bugstack.domain.order.model.valobj.MarketTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopCartEntity {

    private String userId;

    private String productId;
    // 拼团组队ID，可为空，为空的时，则为用户首次创建拼团
    private String teamId;

    // 活动ID，来自于页面调用拼团试算后，获得的活动ID信息
    private Long activityId;

    // 营销类型，无营销，拼团营销
    private MarketTypeVO marketTypeVO;




}
