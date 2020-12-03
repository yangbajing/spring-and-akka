#!/bin/sh

psql -U postgres -d postgres -c "ALTER DATABASE template1 SET timezone TO 'Asia/Chongqing';"
psql -U postgres -d template1 -c "create extension adminpack;"
psql -U postgres -d template1 -f /data/init.sql

psql -U postgres -d postgres -c "create user yj with nosuperuser replication encrypted password 'YJ.2020';"
psql -U postgres -d postgres -c "create database yj_platform owner=yj template=template1;"
psql -U postgres -d postgres -c "create database auth_server owner=yj template=template1;"

psql -U yj -d yj_platform -f /data/yj_platform-ddl.sql
psql -U yj -d auth_server -f /data/auth-server.sql

