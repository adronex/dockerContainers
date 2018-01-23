#!/usr/bin/env bash
echo Waiting for rabbitmq service start...;
while ! nc -z rabbitmq 5672;
do
  sleep 3;
done;
echo Connected!;