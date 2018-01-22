FROM openjdk:8u151-jdk-alpine
RUN apk --update add mysql-client
ADD wait-database.sh /usr/local/bin/
ADD /build/libs/rabbitReceiver-1.0-SNAPSHOT-all.jar /www/
CMD java \
    -Djava.security.egd=file:/dev/./urandom \
    -jar /www/rabbitReceiver-1.0-SNAPSHOT-all.jar