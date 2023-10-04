# Build stage
FROM maven:3.8.6-openjdk-11-slim AS build
# Copy the code over
COPY frontend/src /home/app/src/
COPY frontend/pom.xml /home/app/
RUN mvn -f /home/app/pom.xml install

# Package stage
FROM openjdk:11-jre-slim
ENV KVS_HOME /cs380d-f23/project1
COPY --from=build /home/app/target/frontend-1.0-SNAPSHOT.jar ${KVS_HOME}/frontend.jar

USER root
WORKDIR $KVS_HOME

ENTRYPOINT ["java","-jar", "frontend.jar"]