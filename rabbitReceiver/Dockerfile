FROM openjdk:8u151-jdk-alpine
RUN apk --update add mysql-client
ADD wait-rabbit.sh /usr/local/bin/
ADD wait-database.sh /usr/local/bin/
ADD /build/libs/rabbitReceiver-1.0-SNAPSHOT-all.jar /www/
CMD sh /usr/local/bin/wait-rabbit.sh && \
    sh /usr/local/bin/wait-database.sh && java \
    -Djava.security.egd=file:/dev/./urandom \
    -jar /www/rabbitReceiver-1.0-SNAPSHOT-all.jar