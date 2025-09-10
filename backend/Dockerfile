# Dockerfile
FROM openjdk:17-jdk-slim

# 设置时区
ENV TZ=Asia/Shanghai \
    JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=prod

# 配置时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone


COPY blog-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
