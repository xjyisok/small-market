package cn.bugstack.infrastructure.adapter.repository;

import cn.bugstack.domain.goods.adapter.repository.IGoodesRepository;
import cn.bugstack.infrastructure.dao.IOrderDao;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
@Repository
public class GoodsRepositoryImpl implements IGoodesRepository {
    @Resource
    private IOrderDao orderDao;
    @Override
    public void changeOrderDealDone(String tradeNo) {
        orderDao.changeOrderDealDone(tradeNo);
    }
}
