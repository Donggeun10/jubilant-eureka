package com.example.chatter;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(servers = {
    @Server(url = "/", description = "Default Server URL")
})
@SpringBootApplication
public class ChatbackendApplication {
    /**
     * https://infinitecode.tistory.com/60
     * https://happyzodiac.tistory.com/74?category=1191435
     * https://khdscor.tistory.com/121
     * https://jiangxy.github.io/websocket-debug-tool/
     * https://medium.com/microservices-architecture/hazelcast-messaging-273451ec110a
     * https://refactorfirst.com/spring-boot-websockets-stomp-notifications
     * https://dev-coco.tistory.com/133
     * */
    public static void main(String[] args) {
        SpringApplication.run(ChatbackendApplication.class, args);
    }

}
