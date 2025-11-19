package cn.bugstack.domain.order.adapter.event;

import cn.bugstack.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PaySuccessMesageEvenet extends BaseEvent<PaySuccessMesageEvenet.PaySuccessMessage> {
    @Override
    public EventMessage<PaySuccessMessage> buildEventMessage(PaySuccessMessage data) {
        return EventMessage.<PaySuccessMessage>builder()
                .id(RandomStringUtils.randomAlphanumeric(11))
                .timestamp(new Date())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return "pay_success";
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaySuccessMessage {
        private String userId;
        private String tradeNo;
    }
}
