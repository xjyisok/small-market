package cn.bugstack.domain.goods.service;

import cn.bugstack.domain.goods.adapter.repository.IGoodesRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class GoodsServiceImpl implements IGoodsService{
    @Resource
    private IGoodesRepository  repository;
    @Override
    public void changeOrderDealDone(String tradeNo) {
        repository.changeOrderDealDone(tradeNo);
    }
}
