#!/bin/bash
# Prerequisites
# SBT (https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html)
# Running spark cluster on a docker

help()
{
   echo 
   echo "Usage example: ./submit.sh b=0 e=1 d=2"
   echo "options:"
   echo "b     Build app with SBT assembly. Values 0 and 1"
   echo "d     DRIVER memory in GB."
   echo "e     EXECUTOR memory in GB."
   echo "p     Properties file. Optional"
   echo
   exit
}

if [ $# -lt 3 ]
  then
    help	
fi

for ARGUMENT in "$@"
do

    KEY=$(echo $ARGUMENT | cut -f1 -d=)
    VALUE=$(echo $ARGUMENT | cut -f2 -d=)   

    case "$KEY" in
            b)    BUILD=${VALUE} ;;
            e)    EXECUTOR=${VALUE} ;;
            d)    DRIVER=${VALUE} ;;
            p)    PROP=${VALUE} ;;             
            *)    echo "Unknown parameter"; help
    esac    
done

if [ "$EXECUTOR" -le 0 ] || [ "$DRIVER" -le 0 ] || ( [ $BUILD -ne "1" ] && [ $BUILD -ne "0" ] )
then
	help
fi
if [ -z "$PROP" ] 
then
	PROP="./src/main/resources/consumer.properties"
fi
echo 


if [ "$BUILD" == "1" ]
then
    sbt assembly
fi
docker exec spark-master mkdir /deploy
docker cp ./target/scala-2.11/entity-assembly-0.1.jar spark-master:/deploy/entity_resolution.jar
docker exec spark-master /spark/bin/spark-submit \
  --executor-memory ${EXECUTOR}g \
  --driver-memory ${DRIVER}g \
  --class "com.synechron.entity.entityresolution.ResolutionMain" \
  /deploy/entity_resolution.jar
