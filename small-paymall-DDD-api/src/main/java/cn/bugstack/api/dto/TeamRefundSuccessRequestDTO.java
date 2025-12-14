package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamRefundSuccessRequestDTO {
    /**退单类型*/
    private String type;
    private String teamId;
    private String userId;
    private String orderId;
    private Long activityId;
    private String outTradeNo;

}
