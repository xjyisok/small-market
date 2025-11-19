package cn.bugstack.domain.order.adapter.port;

import cn.bugstack.domain.order.model.entity.ProductEntity;
import org.springframework.stereotype.Service;

public interface IProductPort {
    ProductEntity queryProductByProductId(String productId);
}
