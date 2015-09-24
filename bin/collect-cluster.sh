#!/usr/bin/env bash

/usr/lib/spark/bin/spark-submit \
     --class "com.naughtyzombie.thinkdvr.Collect" \
     --master ${SPARK_MASTER:-local[4]} \
     target/scala-2.10/thinkdvr-classifier-assembly-1.0.jar \
     ${YOUR_OUTPUT_DIR:-/tmp/tweets} \
     ${NUM_TWEETS_TO_COLLECT:-100} \
     ${OUTPUT_FILE_INTERVAL_IN_SECS:-10} \
     ${OUTPUT_FILE_PARTITIONS_EACH_INTERVAL:-2} \
     --consumerKey ${TWITTER_CONSUMER_KEY} \
     --consumerSecret ${TWITTER_CONSUMER_SECRET} \
     --accessToken ${TWITTER_ACCESS_TOKEN}  \
     --accessTokenSecret ${TWITTER_ACCESS_SECRET}
