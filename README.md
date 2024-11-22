# Chatting Backend Server Features
- RESTful API Server Sample code based on Spring boot
- ~~Major Exceptions are managed by RestControllerAdvice and ExceptionHandler~~
- Database such as H2DB is accessed such as insert, select, update and delete (CRUD) via Spring Data JPA
- ~~It controls race condition using LockModeType.PESSIMISTIC_WRITE provided JPA~~
- API Documentation using Springdoc-openapi
- Code simplification using Lombok
- Container creation and execution using Docker
- ~~Logging using Aspect and @Around~~

## 1. Frameworks And Tools
- JDK 21
- spring-boot 3.3.5
- spring-boot-web
- spring-boot-data-jpa
- ~~spring-boot-security~~
- ~~spring-boot-cache~~
- Camel
- Hazelcast
- springdoc-openapi
- h2database
- lombok

## 2. Api Spec URL
- http://localhost:8080/swagger-ui.html

## 3. Endpoints
- GET /api/v1/announcements

## 4. Docker container creation and execution command 

```
docker build -t chat-ws-api:local .  && docker run -p 9090:8080  -e"SPRING_PROFILES_ACTIVE=local"  chat-ws-api:local
docker-compose up
```

## 5. Definition of key problems and solution strategies
- Race condition problem of database access
  - Apply to restrict access to other sessions during change through lock such as LockModeType.PESSIMISTIC_WRITE (for update) in DB table row
- Lack of memory problem by increasing platform threads
  - In order to reduce memory usage per request, using virutal thread
  - Parallel processing through jdk.virtualThreadScheduler.parallelism setting, default value is number of cpu core
- Security Problem
  - Apply universal security configuration to handle spring security
  - Only authenticated users can access the API through basic authentication or OAuth Token
- Problem of frequently access database for authentication in short time
  - Apply local cache through Ehcache
  - Apply cache data sharing through Hazelcast clustering
- Instance startup time and library loading issues
  - Minimize instance startup time through image creation through Graalvm AOT compilation

## 6. Test account and method  
- account
  - robot / play
  - sam / ground
- method
  - swagger-ui.html

## 7. Chatting Room Status table based on STOMP Event 
| Stomp event | Chatting Room Status |
|-------------|----------------------|
| connection  |                      |
| subscribe   | Enter the room       |
| unsubscribe | Exit the room        |
| disconnect  | Exit the room        |

