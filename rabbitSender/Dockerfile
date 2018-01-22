FROM openjdk:8u151-jdk-alpine
ADD /target/rabbitSender-1.0-SNAPSHOT-jar-with-dependencies.jar /www/
CMD java \
    -Djava.security.egd=file:/dev/./urandom \
    -jar /www/rabbitSender-1.0-SNAPSHOT-jar-with-dependencies.jar