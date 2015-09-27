%SPARK_HOME%/bin/spark-submit ^
     --class "com.naughtyzombie.thinkdvr.Predict" ^
     --master local[4] ^
     target/scala-2.10/thinkdvr-classifier-assembly-1.0.jar ^
     target/proc_tweets ^
     7 ^
     --consumerKey %TWITTER_CONSUMER_KEY% ^
     --consumerSecret %TWITTER_CONSUMER_SECRET% ^
     --accessToken %TWITTER_ACCESS_TOKEN%  ^
     --accessTokenSecret %TWITTER_ACCESS_SECRET%
