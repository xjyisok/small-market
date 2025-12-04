package cn.bugstack.trigger.listener;

import cn.bugstack.api.dto.NotifyRequestDTO;
import cn.bugstack.domain.order.service.IOrderService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class TeamSuccessTopicListener {
    @Resource
    private IOrderService orderService;
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value ="${spring.rabbitmq.config.consumer.topic_team_success.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_team_success.exchange}",type = ExchangeTypes.TOPIC),
                    key="${spring.rabbitmq.config.consumer.topic_team_success.routing_key}"
            )
    )
    public void Listener(String message) {
        try {
            log.info("拼团回调，组队完成，开始结算:{}", message);
            NotifyRequestDTO notifyRequestDTO = JSON.parseObject(message, NotifyRequestDTO.class);
            orderService.changeOrderMarketSettlement(notifyRequestDTO.getOutTradeNoList());
            log.info("拼团回调，组队完成，结算成功");
        }catch (Exception e) {
            log.error("拼团回调，组队完成，结算失败{}",message,e);
        }
    }
}
