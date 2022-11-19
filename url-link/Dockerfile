FROM openjdk:17-alpine
COPY target/url-link.jar url-link.jar
ENTRYPOINT ["java","--add-opens","java.base/java.lang=ALL-UNNAMED","--add-opens","java.base/java.util=ALL-UNNAMED","-jar","/url-link.jar"]