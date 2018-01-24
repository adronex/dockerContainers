#!/usr/bin/env bash
echo Waiting for rabbitmq service start...;
while ! nc -z "$RABBIT_HOST" "$RABBIT_PORT";
do
  printf "."
  sleep 3;
done;
echo Connected!;