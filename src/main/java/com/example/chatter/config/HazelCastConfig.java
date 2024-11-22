package com.example.chatter.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class HazelCastConfig {

    @Bean("hazelcastLocalInstance")
    public HazelcastInstance hazelcastLocalInstance() {

        Config config = new Config();
        config.setClusterName("hazelcast-local-cluster");
        config.addReliableTopicConfig(new ReliableTopicConfig("chat-topic"));

        NetworkConfig networkConfig = config.getNetworkConfig();
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(true);

        return Hazelcast.newHazelcastInstance(config);

    }

}
