FROM openjdk:17-alpine
COPY target/eureka-server01.jar eureka-server01.jar
ENTRYPOINT ["java","--add-opens","java.base/java.lang=ALL-UNNAMED","--add-opens","java.base/java.util=ALL-UNNAMED","-jar","/eureka-server01.jar"]