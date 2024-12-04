# Chatting Backend Server 

## 1. Frameworks And Tools
- JDK 21
- Spring-boot 3.3.5
- Spring-boot-web
- Spring-boot-data-jpa
- Camel
- Hazelcast
- Springdoc-openapi
- H2database
- Lombok

## 2. Test URL
- Api Spec : http://localhost:8080/swagger-ui.html
- Websocket debug tool : https://jiangxy.github.io/websocket-debug-tool/
```
test sample 
 ws://localhost:8080/ws-stomp
 /sub/chat/8f4cf2cd-15a2-42b0-8c01-47129de32a0f
 /pub/chat/8f4cf2cd-15a2-42b0-8c01-47129de32a0f

 {"type" : "TALK", "chatRoomId" : "8f4cf2cd-15a2-42b0-8c01-47129de32a0f", "sender" : "apple", "message" : "hello"}
 {"type" : "ENTER", "chatRoomId" : "8f4cf2cd-15a2-42b0-8c01-47129de32a0f", "sender" : "apple", "message" : "hello everyone"}
 {"type" : "EXIT", "chatRoomId" : "8f4cf2cd-15a2-42b0-8c01-47129de32a0f", "sender" : "apple"}

```

## 3. Endpoints
- POST /api/v1/chat/room
- GET /api/v1/chat/rooms/member-id/{memberId}
- POST /api/v1/chat/room/join/room-id/{roomId}
- POST /api/v1/chat/room/leave/room-id/{roomId}

## 4. Docker container creation and execution command 

```
docker build -t chat-ws-api:local .  && docker run -p 9090:8080  -e"SPRING_PROFILES_ACTIVE=local"  chat-ws-api:local
docker-compose up
```

## 5. Sequence Table

| Description                   | Method                                       | Remark                                                   |   
|-------------------------------|----------------------------------------------|----------------------------------------------------------|
| 1. 채팅방을 만들고 사용자를 등록한다.        | POST http://chat/room                        |                                                          |
| 2. 사용자가 참여한다.                 | POST http://chat/room/join/room-id/{roomId}  | 최대인원 100명 초과 여부를 확인한다. <br/> 사용자를 접속한 상태로 기록한다. (Active) |
| 2.1 사용자가 등록된 방 목록을 조회 후 참여한다. | GET http://chat/room/member-id/{memberId}    |                                                          |
| 3. 사용자가 입장한다.                 | ws://subscribe, MessageType.ENTER            | 사용자를 접속한 상태로 기록한다. (Active)                              |
| 4. 사용자가 대화를 한다.               | ws://publish , MessageType.TALK              |                                                          |
| 5. 사용자가 퇴장한다.                 | ws://unsubscribe, MessageType.EXIT           | 사용자를 접속하지 않은 상태로 기록한다. (InActive)                        |
| 6. 사용자가 방을 떠난다.               | POST http://chat/room/leave/room-id/{roomId} |                                                          |


## 6. Architecture Diagram
- Chatting Server Architecture Diagram
- <img src="img_1.png" width="700">
