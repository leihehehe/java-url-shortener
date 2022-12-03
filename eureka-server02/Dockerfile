FROM openjdk:17-alpine
COPY target/eureka-server02.jar eureka-server02.jar
ENV EUREKA1_ADDRESS=$EUREKA1_ADDRESS
ENTRYPOINT ["java","--add-opens","java.base/java.lang=ALL-UNNAMED","--add-opens","java.base/java.util=ALL-UNNAMED","-jar","/eureka-server02.jar"]