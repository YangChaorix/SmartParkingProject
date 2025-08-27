# 使用JDK作为基础镜像
FROM openjdk:17-jdk

# 设置工作目录
WORKDIR /app

# 将可执行的JAR文件从target目录复制到容器中
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用程序
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]