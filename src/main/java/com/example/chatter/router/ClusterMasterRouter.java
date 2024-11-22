package com.example.chatter.router;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hazelcast.HazelcastConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClusterMasterRouter extends RouteBuilder {


    @Value("${app.completionInterval.masterRouter:300000}")
    private long completionInterval;

    @Override
    public void configure() throws Exception {
        from("timer:heartbeat?period=60000")
            .routeId("heartbeat")
            .log("HeartBeat route (timer) ...");

        from("master:{{camel.cluster.controller.namespace}}:timer:clustered?period=60000")
            .routeId("clustered")
            .log("Clustered route (timer) ...");

        fromF("master:{{camel.cluster.controller.namespace}}:hazelcast-%schat-topic?hazelcastInstance=#hazelcastLocalInstance", HazelcastConstants.TOPIC_PREFIX)
            .routeId("chatSubscription")
            .choice()
            .when(header(HazelcastConstants.LISTENER_ACTION).isEqualTo(HazelcastConstants.RECEIVED))
            .log("...message received ${body}")
            .otherwise()
            .log("...this should never have happened");



        // start from a timer
//        from("master:{{camel.cluster.controller.namespace}}:google-pubsub:{{spring.cloud.gcp.project-id}}:realtime.stat.consumer").routeId("realtimeSubscription")
//            // and call the bean
//            .aggregate(new GroupedAggregation()).constant(true)
//            // wait for 10 seconds to aggregate, input unit is millisecond
//            .completionInterval(completionInterval)
//            // and print it to system out via stream component
//            .split().body()
//            .process(exchange -> {
//                RealtimeCount incoming = exchange.getIn().getBody(RealtimeCount.class);
//
//                String key = String.format("%s:%s:%s:%s", incoming.getChannel(), incoming.getGameCode(), incoming.getDeviceOs(), incoming.getBuildCode());
//                log.debug("key: {}", key);
//
//                exchange.getIn().setHeader(RedisConstants.KEY, key);
//                exchange.getIn().setHeader(RedisConstants.VALUE, new ObjectMapper().writeValueAsString(incoming));
//                exchange.getIn().setHeader(RedisConstants.CHANNEL, key);
//
//            })
//            .setHeader(RedisConstants.COMMAND, constant("SETEX"))
//            .setHeader(RedisConstants.KEY, header(RedisConstants.KEY))
//            .setHeader(RedisConstants.VALUE, header(RedisConstants.VALUE))
//            .setHeader(RedisConstants.TIMEOUT, constant(60*10))  // 10 minutes
//            .to("spring-redis://{{spring.data.redis.host}}:{{spring.data.redis.port}}?redisTemplate=#redisTemplate")
//            .setHeader(RedisConstants.COMMAND, constant("PUBLISH"))
//            .setHeader(RedisConstants.MESSAGE, header(RedisConstants.VALUE))
//            .to("spring-redis://{{spring.data.redis.host}}:{{spring.data.redis.port}}?redisTemplate=#redisTemplate")
//            .sample(30)
//            .log(LoggingLevel.INFO, "Redis result: ${body}")
//            .end();
    }
}