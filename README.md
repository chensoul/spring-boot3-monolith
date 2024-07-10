# Spring Boot3 Monolith

## 项目介绍

| 模块名称        | 技术选型              | 使用版本   | 最新版本                                                                                                                                                                                                                              | 备注 |
|-------------|-------------------|--------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----|
| 语言          | Java              | 21     |                                                                                                                                                                                                                                   |    |
| 构建工具        | Maven             | 3.9.6  |                                                                                                                                                                                                                                   |    |
| 容器编排        | Docker            | 25.0.5 |                                                                                                                                                                                                                                   |    |
| 数据库         | Postgres          | 16     |                                                                                                                                                                                                                                   |    |
| 缓存          | Redis             | 7.2    |                                                                                                                                                                                                                                   |    |
| 消息队列        | Rabbitmq          | 3.13.0 |                                                                                                                                                                                                                                   |    |
| 开发框架        | Spring Boot       | 3.3.1  | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&versionPrefix=3&metadataUrl=https://s01.oss.sonatype.org/content/repositories/releases/org/springframework/boot/spring-boot-dependencies/maven-metadata.xml"> |    |
| 在线文档        | SpringDoc OpenApi | 2.6.0  | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&metadataUrl=https://oss.sonatype.org/content/repositories/releases/org/springdoc/springdoc-openapi/maven-metadata.xml">                                       |    |
| 限流          | Bucket4j          | 8.10.1 | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&metadataUrl=https://oss.sonatype.org/content/repositories/releases/com/bucket4j/bucket4j-core/maven-metadata.xml">    <br/>                                   |    |
| Profiling工具 | pyroscope         | 0.14.0 | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&metadataUrl=https://oss.sonatype.org/content/repositories/releases/io/pyroscope/agent/maven-metadata.xml">                                                    

## 特性

- Error Handler
- WebMvc
    - I18N
- RestTemplate
- WebClient
- Validation
- SpringDoc
- Jackson
- JPA
- Cache
- RabbitMQ
- RateLimit
- Pyroscope
- Github Action
- Docker Compose
- Makefile

## 目录

- 工程化：
    - Git
    - Maven
    - Gradle

- 单体应用：Spring Boot 3.x+
    - [x] chapter01: Restful
        - [x] Exception
        - [ ] JSON
        - [ ] I18N
        - [ ] Validation
        - [ ] Unit Test
    - [x] chapter02: OpenAPI
    - [x] chapter03: Persistence: MongoDB & Postgres

- 分布式：Distribution
    - [ ] chapter04: Distributed Cache
    - [ ] chapter05: Distributed Session
    - [ ] chapter06: Distributed Lock
    - [ ] chapter07: Distributed Message
    - [ ] chapter08: Distributed Search
    - [ ] chapter09: Distributed Transaction

- 微服务部署：Spring Cloud 2023.x+
    - [x] chapter11: Service Discovery with Netflix Eureka
    - [x] chapter12: Securing Access with OAuth2
    - [x] chapter13: API Gateway with Spring Cloud Gateway
    - [x] chapter14: Centralized Configuration with Spring Cloud Config
    - [x] chapter15: All in One with Spring Cloud
    - [ ] chapter16: Remote Service Call with OpenFeign and Load Balancer
    - [ ] chapter17: Circuit Breaker with Resilience4j
    - [ ] chapter18: Distributed Tracing with Zipkin
    - [ ] chapter19: Centralized Logging with the EFK Stack
    - [ ] chapter20: Monitoring Microservices

- 云原生部署：Cloud Native
    - [ ] Docker
    - [ ] Kubernetes
    - [ ] Istio
    - [ ] Mesos
    - [ ] Native Compiled Java Microservices

## 快速开始

### 环境搭建

- [Git](https://git-scm.com/downloads)
- [Docker](https://docs.docker.com/get-docker/)
- [Java](https://www.azul.com/downloads/#zulu)
- [Curl](https://curl.haxx.se/download.html)
- [Jq](https://stedolan.github.io/jq/download/)
- [Spring Boot CLI](https://docs.spring.io/spring-boot/docs/3.0.4/reference/html/getting-started.html#getting-started.installing.cli)
- [Siege](https://github.com/JoeDog/siege#where-is-it)
- [Helm](https://helm.sh/docs/intro/install/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl-macos/)
- [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- [Istioctl](https://istio.io/latest/docs/setup/getting-started/#download)

安装软件：

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

brew tap spring-io/tap && \
brew tap homebrew/cask-versions && \
brew install docker && \
brew install --cask temurin8 && \
brew install jq && \
brew install spring-boot && \
brew install siege && \
brew install helm && \
brew install minikub && \
brew install kubectl && \
brew install istioctl

echo 'export JAVA_HOME=$(/usr/libexec/java_home -v8)' >> ~/.bash_profile
source ~/.bash_profile
```

验证版本：

```bash
git version && \
docker version -f json | jq -r .Client.Version && \
java -version 2>&1 | grep "openjdk version" && \
mvn -v | grep "Maven" && \
curl --version | grep "curl" | sed 's/(.*//' && \
jq --version && \
spring --version && \
siege --version 2>&1 | grep SIEGE && \
helm version --short && \
kubectl version --client -o json | jq -r .clientVersion.gitVersion && \
minikube version | grep "minikube" && \
istioctl version --remote=false
```

### 本地运行

```bash
docker-compose -f docker-compose.yml -p postgres up -d
docker-compose -f docker-compose.yml -p redis up -d

mvn clean -DskipTests spring-boot:run
```

### Docker 中运行

构建镜像并运行测试：

```bash
docker build -t chensoul/spring-boot3-monolith-test --progress=plain --no-cache --target=test .
```

构建镜像并运行启动应用：

```bash
docker build . --file Dockerfile.simple -t chensoul/spring-boot3-monolith

docker compose up --build
```

### K8s 中运行

构建镜像并推送：

```bash
mvn clean pakcage -DskipTests

docker build -t spring-boot3-monolith .
docker tag spring-boot3-monolith:latest chensoul/spring-boot3-monolith:latest
docker login
docker push chensoul/spring-boot3-monolith:latest
```

#### 部署到 minikube

参考 [从本地主机到云：使用 Docker Desktop 在 Kubernetes 上部署 Spring Boot + MySQL 应用程](https://levelup.gitconnected.com/from-localhost-to-the-cloud-deploying-spring-boot-mysql-app-on-kubernetes-with-docker-desktop-a-8c51f9cd23fa)：

```bash
minikube start 
minikube addons enable ingress

cd src/main/kubernetes
kubectl apply -f postgres-deployment.yaml
kubectl apply -f postgres-service.yaml
kubectl apply -f redis-deployment.yaml
kubectl apply -f redis-service.yaml
kubectl apply -f spring-boot3-monolith-deployment.yaml
kubectl apply -f spring-boot3-monolith-service.yaml
kubectl apply -f spring-boot3-monolith-ingress.yaml

kubectl get pods
kubectl get deployments
kubectl get services
kubectl get ingress

minikube service monolith-service --url
#http://127.0.0.1:57936
```

#### 部署到本地 k8s

首先，在 Docker Desktop 中[打开 Kubernetes](https://docs.docker.com/desktop/kubernetes/#install-and-turn-on-kubernetes)。

```bash
cd src/k8s
kubectl apply -f postgres-deployment.yaml
kubectl apply -f postgres-service.yaml
kubectl apply -f redis-deployment.yaml
kubectl apply -f redis-service.yaml
kubectl apply -f spring-boot3-monolith-deployment.yaml
kubectl apply -f spring-boot3-monolith-service.yaml
kubectl apply -f spring-boot3-monolith-ingress.yaml

kubectl get pods
kubectl get deployments
kubectl get services
kubectl get ingress
```

访问服务：

```bash
 curl --request GET \
  --url http://localhost:30001/actuator/health \
  --header 'content-type: application/json'
```

## 参考

- https://github.com/xsreality/spring-modulith-with-ddd
- https://github.com/chensoul/Microservices-with-Spring-Boot-and-Spring-Cloud-Third-Edition
- https://github.com/in28minutes/spring-microservices-v3
- [Spring Boot 高级实践：使用 Docker、Zipkin 和 100% 代码覆盖率构建模块化应用程序](https://www.makariev.com/blog/advanced-spring-boot-structure-clean-architecture-modulith/)
- [告别重复：在 Spring Boot 中构建通用库](https://medium.com/@jovanoskivasko14/say-goodbye-to-repetition-building-a-common-library-in-spring-boot-44e709b6f67d)
- [Integration Testing With Keycloak, Spring Security, Spring Boot, and Spock Framework](https://dzone.com/articles/integration-testing-with-keycloak-spring-security)
- [Spring Boot 3.2 综合指南，包含 Java 21、虚拟线程、Spring Security、PostgreSQL、Flyway、缓存、Micrometer、Opentelemetry、JUnit 5、RabbitMQ、Keycloak 集成等！(10/17)](https://itnext.io/exploring-a-base-spring-boot-application-with-java-21-virtual-thread-spring-security-flyway-c0fde13c1eca)
- [使用 Docker 容器化并运行 Spring Boot 应用程序](https://blog.chensoul.cc/posts/2024/07/09/docker-for-spring-boot/)