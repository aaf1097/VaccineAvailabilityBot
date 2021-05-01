FROM maven:3.6.3-jdk-14 as build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install 

FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/VaccineAvailabilityBot-0.0.1.jar /usr/local/lib/
EXPOSE 8080
CMD ["java", "-jar", "/usr/local/lib/VaccineAvailabilityBot-0.0.1.jar"]
