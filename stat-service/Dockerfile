FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar stat-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","stat-service-0.0.1-SNAPSHOT.jar"]