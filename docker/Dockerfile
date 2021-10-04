#builk-stage
FROM zelejs/allin-web:alpine-m2 AS M2
FROM maven:3.6-openjdk-11-slim AS build

RUN mkdir $MAVEN_CONFIG

## src
WORKDIR /usr/src

COPY . .

## install
COPY --from=M2 /root/.m2 $MAVEN_CONFIG
RUN rm -rf /root/.m2/repository/com/jfeat
RUN mvn -Dmaven.main.skip=true -Dmaven.test.skip=true install 

# ## package
WORKDIR /usr/src/mall-webapp
RUN mvn -Dmaven.main.skip=true -Dmaven.test.skip=true package

#final-stage
FROM tomcat:jre11-temurin
COPY --from=build /usr/src/mall-webapp/target/*.war /usr/local/tomcat/webapps/ROOT.war
