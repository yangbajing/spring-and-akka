#!/bin/sh

psql -U postgres -d template1 -c "create extension adminpack;"
psql -U postgres -d template1 -f /data/init.sql

psql -U postgres -d postgres -c "create user yj with nosuperuser replication encrypted password 'YJ.2020';"
psql -U postgres -d postgres -c "create database yj_platform owner=yj template=template1;"
psql -U postgres -d postgres -c "ALTER DATABASE yj_platform SET timezone TO 'Asia/Chongqing';"

psql -U yj -d yj_platform -f /data/yj_platform-ddl.sql

