FROM openjdk:17-slim
COPY target/url-account.jar url-account.jar
ENTRYPOINT ["java","--add-opens","java.base/java.lang=ALL-UNNAMED","--add-opens","java.base/java.util=ALL-UNNAMED","-jar","/url-account.jar"]