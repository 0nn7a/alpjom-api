# ---- Build stage ----
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# 先只複製跟相依套件有關的檔案，讓 Docker layer cache 生效
# （之後只改 src 程式碼時，不用重新下載所有 Maven 依賴，build 會快很多）
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# 再複製原始碼並編譯
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ---- Run stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 用 wildcard 複製，不用擔心版本號改變導致檔名對不上
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]