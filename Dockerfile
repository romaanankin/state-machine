FROM openjdk:8-jre
VOLUME /tmp
COPY target/scala-2.12/app-assembly.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java","-jar","/app.jar"]