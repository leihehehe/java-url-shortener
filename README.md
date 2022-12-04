# Introduction

**Amazing URL Shortener** is an online tool with high availability to shorten a long link and provide detailed statistics.

**Demo**: 

- http://official.urls.fit

- http://short.urls.fit

**Short Link Demo**: `urls.fit/a26B4EM0`

**Detailed Technical Documentation**: https://leihehe.top/2022/12/04/Amazing-URL-Shortener-Technical-Doc/

# Usage

`username: test`

`password: test`

- 🌟🌟**Log in to the system and shorten your long URL in a second**🌟🌟
- Currently, **Account registration** is only available for pre-set phone numbers since the AWS account is in the SMS sandbox. 

- The **Clicks View** service on the Dashboard is not deployed because `Flink` needs to be deployed in a few high-performance servers which would cost much:(. You should be able to see all the related code in the project, also alternately you can run the program on your side to see the effect.

# Technical Stacks Used

**Backend Techniques:** 

- **Framework**: SpringBoot, Spring Cloud
- **Gateway**: Spring Cloud Gateway
- **Message Queues**: RabbitMQ, Kafka
- **Database-related Techs**: Redis, Redisson(operating on Redis), ClickHouse, SpringData JPA, MySQL,  Apache Shardingsphere
- **Data Streaming-processing Framework**: Flink

- **Availability**: resillience4j
- **Others**: Openfeign, etc.

**Frontend:** Angular.js

**DevOps**:

- Jenkins
- Kubernetes