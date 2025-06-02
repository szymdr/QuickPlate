FROM maven:3.8.5-openjdk-17 AS deps  
WORKDIR /app  
COPY pom.xml .  
RUN mvn dependency:go-offline -B  

FROM maven:3.8.5-openjdk-17 AS build  
WORKDIR /app  
COPY --from=deps /root/.m2 /root/.m2  
COPY . .  
RUN mvn clean package -DskipTests  

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]