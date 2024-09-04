FROM openjdk:17
EXPOSE 8081
ADD target/sample-rest-service.jar sample-rest-service.jar 
ENTRYPOINT ["java","-jar","/sample-rest-service.jar"]