package cn.bugstack.infrastructure.gateway;
import cn.bugstack.infrastructure.gateway.dto.ProductDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductRPC {
    public ProductDTO queryProductByProductId(String productId){
        ProductDTO productdto = new ProductDTO();
        productdto.setProductId(productId);
        productdto.setProductName("测试商品");
        productdto.setProductDesc("这是一个测试商品");
        productdto.setPrice(new BigDecimal("1.68"));
        return productdto;
    }
}
