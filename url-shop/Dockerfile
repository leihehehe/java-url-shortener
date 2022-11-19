FROM openjdk:17-alpine
COPY target/url-shop.jar url-shop.jar
ENTRYPOINT ["java","--add-opens","java.base/java.lang=ALL-UNNAMED","--add-opens","java.base/java.util=ALL-UNNAMED","-jar","/url-shop.jar"]