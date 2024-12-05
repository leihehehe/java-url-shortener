# Introduction

**Amazing URL Shortener** is an online tool with high availability to shorten a long link and provide detailed statistics. It is a full stack project.

~~**Demo**:~~ 

- ~~http://official.urls.fit~~

- ~~http://short.urls.fit~~

~~**Short Link Demo**: `urls.fit/a26B4EM0`~~

**Detailed Technical Documentation**: https://leihehe.top/2022/12/04/Amazing-URL-Shortener-Technical-Doc/

# Usage

`username: test`

`password: test`

- 🌟🌟**Log in to the system and shorten your long URL in a second**🌟🌟
- Currently, **Account registration** is only available for pre-set phone numbers since the AWS account is in the SMS sandbox. If you are interested in it, please feel free to email me, I'm happy to add your phone number in the SMS sandbox. 

- The **Clicks View** service on the Dashboard is not working because `Flink` needs to be deployed in a few high-performance servers which would cost much:(. You should be able to see all the related code in the project, also alternately you can run the program on your side to see the effect.

# Technical Stacks Used

**Backend Techniques:** 

- **Framework**: SpringBoot, Spring Cloud
- **Discovery Framework** Netflix Eureka
- **Gateway**: Spring Cloud Gateway
- **Message Queues**: RabbitMQ, Kafka
- **Database-related Techs**: Redis, Redisson(operating on Redis), ClickHouse, SpringData JPA, MySQL,  Apache Shardingsphere
- **Data Streaming-processing Framework**: Flink
- **Availability**: resillience4j
- **Others**: Openfeign, etc.

**Frontend:** Angular.js

**DevOps**:

- Docker
- Jenkins
- Kubernetes - 1 master node + 2 worker nodes
- AWS Services - EC2, AWS Route 53, EC2 and ELB
