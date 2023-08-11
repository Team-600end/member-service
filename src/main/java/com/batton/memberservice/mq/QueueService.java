package com.batton.memberservice.mq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final RabbitAdmin rabbitAdmin;

    public void createQueueForMember(Long memberId) {
        Queue queue = new Queue("user.queue." + memberId.toString(), true, false, false);
        rabbitAdmin.declareQueue(queue);

        // Topic Exchange 생성
        TopicExchange topicExchange = new TopicExchange("notice-exchange");

        // 큐와 Topic Exchange를 바인딩하고 라우팅 키 설정
        String routingKey = "user.key" + memberId.toString();
        Binding binding = BindingBuilder.bind(queue).to(topicExchange).with(routingKey);

        rabbitAdmin.declareBinding(binding);
    }
}
