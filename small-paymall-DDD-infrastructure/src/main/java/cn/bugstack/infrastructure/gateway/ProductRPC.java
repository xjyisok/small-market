package cn.bugstack.infrastructure.gateway;
import cn.bugstack.infrastructure.gateway.dto.ProductDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductRPC {
    public ProductDTO queryProductByProductId(String productId){
        ProductDTO productdto = new ProductDTO();
        productdto.setProductId(productId);
        productdto.setProductName("手写MyBatis");
        productdto.setProductDesc("手写MyBatis");
        productdto.setPrice(new BigDecimal("100"));
        return productdto;
    }
}
