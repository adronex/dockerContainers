FROM openjdk:8u151-jdk-alpine
ADD /wait-rabbit.sh /usr/local/bin/
ADD /target/rabbitSender-1.0-SNAPSHOT-jar-with-dependencies.jar /www/
CMD sh /usr/local/bin/wait-rabbit.sh && java \
    -Djava.security.egd=file:/dev/./urandom \
    -jar /www/rabbitSender-1.0-SNAPSHOT-jar-with-dependencies.jar