%SPARK_HOME%/bin/spark-submit ^
     --class "com.naughtyzombie.thinkdvr.ExamineAndTrain" ^
     --master local[4] ^
     target/scala-2.10/thinkdvr-classifier-assembly-1.0.jar ^
     target/tweets/tweets*/part-* ^
     target/proc_tweets ^
     20 ^
     10
