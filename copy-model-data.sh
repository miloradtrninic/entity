#!/bin/bash
docker exec namenode mkdir model
docker cp data/ namenode:/model/
docker exec namenode hdfs dfs -mkdir /model/news/
docker exec namenode hdfs dfs -put -f /model/data/ /model/news/
docker exec namenode hdfs dfs -ls /model/news/data
