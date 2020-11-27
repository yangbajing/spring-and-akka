#!/bin/sh

psql -U postgres -d template1 -c "create extension adminpack;"
psql -U postgres -d template1 -f /data/init.sql

psql -U postgres -d postgres -c "create user hj with nosuperuser replication encrypted password 'HJ.2020';"
psql -U postgres -d postgres -c "create database hj_platform owner=hj template=template1;"
psql -U postgres -d postgres -c "ALTER DATABASE hj_platform SET timezone TO 'Asia/Chongqing';"

psql -U hj -d hj_platform -f /data/hj_platform-ddl.sql
