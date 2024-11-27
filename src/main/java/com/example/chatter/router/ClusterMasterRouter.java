package com.example.chatter.router;

import com.example.chatter.component.DataConverter;
import com.example.chatter.stragtegy.ChatMessageAggregator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClusterMasterRouter extends RouteBuilder {

    private final DataConverter dataConverter;

    @Value("${app.completionInterval.masterRouter:10000}")
    private long completionInterval;

    @Override
    public void configure() {
        from("timer:heartbeat?period=60000")
            .routeId("heartbeat")
            .log(LoggingLevel.DEBUG, log, "HeartBeat route (timer) ...");

        from("master:{{camel.cluster.controller.namespace}}:timer:clustered?period=60000")
            .routeId("clustered")
            .log(LoggingLevel.DEBUG, log, "Clustered route (timer) ...");

        fromF("master:{{camel.cluster.controller.namespace}}:hazelcast-%schat-talk-topic?hazelcastInstance=#hazelcastLocalInstance", HazelcastConstants.TOPIC_PREFIX)
            .routeId("chatTalkSubscription")
            .log(LoggingLevel.DEBUG, log, "...message received ${body}")
            .aggregate(new ChatMessageAggregator()).constant(true)
            // wait for 10 seconds to aggregate, input unit is millisecond
            .completionInterval(completionInterval)
            .to("jpa:com.example.chatter.entity.ChatMessage?entityType=java.util.List");

        fromF("master:{{camel.cluster.controller.namespace}}:hazelcast-%schat-enter-topic?hazelcastInstance=#hazelcastLocalInstance", HazelcastConstants.TOPIC_PREFIX)
            .routeId("chatEnterSubscription")
            .log(LoggingLevel.DEBUG, log, "...message received ${body}")
            .aggregate(new ChatMessageAggregator()).constant(true)
            // wait for 10 seconds to aggregate, input unit is millisecond
            .completionInterval(completionInterval)
            .bean(dataConverter,"convertMessageToRoomMember")
            .to("jpa:com.example.chatter.entity.ChatRoomMember?entityType=java.util.List");

    }
}