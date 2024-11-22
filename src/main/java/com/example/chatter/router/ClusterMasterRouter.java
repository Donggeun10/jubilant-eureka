package com.example.chatter.router;

import com.example.chatter.stragtegy.ChatMessageAggregator;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClusterMasterRouter extends RouteBuilder {


    @Value("${app.completionInterval.masterRouter:10000}")
    private long completionInterval;

    @Override
    public void configure() {
        from("timer:heartbeat?period=60000")
            .routeId("heartbeat")
            .log("HeartBeat route (timer) ...");

        from("master:{{camel.cluster.controller.namespace}}:timer:clustered?period=60000")
            .routeId("clustered")
            .log("Clustered route (timer) ...");

        fromF("master:{{camel.cluster.controller.namespace}}:hazelcast-%schat-topic?hazelcastInstance=#hazelcastLocalInstance", HazelcastConstants.TOPIC_PREFIX)
            .routeId("chatSubscription")
            .log("...message received ${body}")
            .aggregate(new ChatMessageAggregator()).constant(true)
            // wait for 10 seconds to aggregate, input unit is millisecond
            .completionInterval(completionInterval)
            .to("jpa:com.example.chatter.entity.ChatMessage?entityType=java.util.List");

    }
}