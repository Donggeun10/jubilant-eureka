package com.example.chatter.stragtegy;

import com.example.chatter.entity.ChatMessage;
import com.hazelcast.topic.impl.DataAwareMessage;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

@Slf4j
public class ChatMessageAggregator implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        DataAwareMessage newBody = newExchange.getIn().getBody(DataAwareMessage.class);
        List<ChatMessage> oldList = new ArrayList<>();
        log.debug("ChatMessageAggregator newExchange: {}", newBody.getMessageObject());
        ChatMessage data = (ChatMessage) newBody.getMessageObject();
        oldList.add(data);
        if(oldExchange == null){
            newExchange.getIn().setBody(oldList);
            log.debug("newBody={}", data);
            return newExchange;
        }else{
            List oldBody = oldExchange.getIn().getBody(List.class);
            oldList.addAll(oldBody);
            log.debug("oldBody.size={}", oldList.size());
            oldExchange.getIn().setBody(oldList);
            return oldExchange;
        }
    }
}
