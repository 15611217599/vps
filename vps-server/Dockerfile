# 使用OpenJDK 21运行时镜像
FROM openjdk:21-jre-slim

# 设置工作目录
WORKDIR /app

# 复制已构建的jar文件（需要先在本地构建）
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
