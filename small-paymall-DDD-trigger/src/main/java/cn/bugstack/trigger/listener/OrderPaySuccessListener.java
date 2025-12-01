package cn.bugstack.trigger.listener;

import cn.bugstack.domain.goods.service.IGoodsService;
import cn.bugstack.domain.order.adapter.event.PaySuccessMesageEvenet;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 支付成功回调消息
 * @create 2024-09-30 09:52
 */
@Slf4j
@Component
public class OrderPaySuccessListener {
    @Resource
    private IGoodsService goodsService;

    @Subscribe
    public void handleEvent(String paySuccessMessage) {
        log.info("收到支付成功消息，可以做接下来的事情，如；发货、充值、开会员、返利 {}", paySuccessMessage);
        PaySuccessMesageEvenet.PaySuccessMessage paySuccessMessageEvent = JSON.parseObject(paySuccessMessage, PaySuccessMesageEvenet.PaySuccessMessage.class);
        log.info("模拟单号 {}", paySuccessMessageEvent.getTradeNo());
        goodsService.changeOrderDealDone(paySuccessMessageEvent.getTradeNo());
    }

}
