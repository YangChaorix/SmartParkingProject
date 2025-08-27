# 使用JDK作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 将Maven的pom.xml和依赖文件复制到容器内
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY server/src/main/resources/application.yml src/main/resources/application.yml

# 运行Maven打包应用，-DskipTests跳过测试
RUN ./mvnw clean package -DskipTests

# 将可执行的JAR文件从target目录复制到容器中
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用程序
ENTRYPOINT ["java","-jar","/app/app.jar"]