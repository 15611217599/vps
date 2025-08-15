FROM openjdk:21-jdk-slim

WORKDIR /app

# 复制Maven配置文件
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# 下载依赖
RUN ./mvnw dependency:go-offline -B

# 复制源代码
COPY src src

# 构建应用
RUN ./mvnw clean package -DskipTests

# 运行应用
EXPOSE 8080
CMD ["java", "-jar", "target/vps-server-0.0.1-SNAPSHOT.jar"]