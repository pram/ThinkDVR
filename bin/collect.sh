#!/usr/bin/env bash

${SPARK_HOME}/bin/spark-submit \
     --class "com.naughtyzombie.thinkdvr.Collect" \
     --master ${YOUR_SPARK_MASTER:-local} \
     target/scala-2.11/thinkdvr-classifier-assembly-1.0.jar \
     ${YOUR_OUTPUT_DIR:-/tmp/tweets} \
     ${NUM_TWEETS_TO_COLLECT:-1000} \
     ${OUTPUT_FILE_INTERVAL_IN_SECS:-10} \
     ${OUTPUT_FILE_PARTITIONS_EACH_INTERVAL:-1} \
     --consumerKey ${TWITTER_CONSUMER_KEY} \
     --consumerSecret ${TWITTER_CONSUMER_SECRET} \
     --accessToken ${TWITTER_ACCESS_TOKEN}  \
     --accessTokenSecret ${TWITTER_ACCESS_SECRET}
