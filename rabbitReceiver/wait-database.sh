#!/bin/bash

echo "Waiting for mysql"
while ! mysqladmin ping -h"$MYSQL_HOST" -P"$MYSQL_PORT" --silent; do
    printf "."
    sleep 3
done

echo -e "\nmysql ready"