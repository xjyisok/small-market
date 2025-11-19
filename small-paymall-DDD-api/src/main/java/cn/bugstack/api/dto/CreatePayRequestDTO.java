package cn.bugstack.api.dto;

import lombok.Data;

@Data
public class CreatePayRequestDTO {
    private String userId;
    // 产品编号
    private String productId;
}
