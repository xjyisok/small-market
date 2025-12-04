package cn.bugstack.trigger.listener;

import cn.bugstack.api.dto.NotifyRequestDTO;
import cn.bugstack.domain.goods.service.IGoodsService;
import cn.bugstack.domain.order.adapter.event.PaySuccessMesageEvenet;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value ="${spring.rabbitmq.config.consumer.topic_order_pay_success.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_order_pay_success.exchange}",type = ExchangeTypes.TOPIC),
                    key="${spring.rabbitmq.config.consumer.topic_order_pay_success.routing_key}"
            )
    )
    public void Listener(String message) {
        try {
            log.info("收到支付成功消息:{}", message);
            PaySuccessMesageEvenet.PaySuccessMessage paySuccessMessage=JSON.parseObject(message, PaySuccessMesageEvenet.PaySuccessMessage.class);
            goodsService.changeOrderDealDone(paySuccessMessage.getTradeNo());
            log.info("模拟发货（如；发货、充值、开户员、返利），单号:{}", paySuccessMessage.getTradeNo());
        }catch (Exception e) {
            log.error("接收支付成功消息失败{}",message,e);
            throw e;
        }
    }

}
