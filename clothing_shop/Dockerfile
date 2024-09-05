FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
EXPOSE 8080
COPY target/clothing_shop-0.0.1-SNAPSHOT.jar clothing_shop.jar
ENTRYPOINT ["java","-jar","/clothing_shop.jar"]