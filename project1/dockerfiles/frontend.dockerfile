# Build stage
FROM maven:3.8.6-openjdk-11-slim AS build
# Copy the code over
COPY java/frontend/src /home/app/frontend/src/
COPY java/frontend/pom.xml /home/app/frontend/
COPY java/pom.xml /home/app/
COPY java/shared/src /home/app/shared/src/
COPY java/shared/pom.xml /home/app/shared/
COPY java/server/src /home/app/server/src
COPY java/server/pom.xml /home/app/server/

RUN mvn -f /home/app/pom.xml install

# Package stage
FROM openjdk:11-jre-slim
ENV KVS_HOME /cs380d-f23/project1
COPY --from=build /home/app/target/frontend-1.0-SNAPSHOT.jar ${KVS_HOME}/frontend.jar

USER root
WORKDIR $KVS_HOME

ENTRYPOINT ["java","-jar", "frontend.jar"]