package com.naughtyzombie.twitter.client

import twitter4j.TwitterFactory
import twitter4j.Twitter
import twitter4j.conf.ConfigurationBuilder

/**
 * Created by pram on 22/09/15.
 */
object TwitterClient {
  def main(args: Array[String]): Unit = {
    val cb = new ConfigurationBuilder()
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey("")
      .setOAuthConsumerSecret("")
      .setOAuthAccessToken("")
      .setOAuthAccessTokenSecret("")
      .setUseSSL(true)
//    val tf = new TwitterFactory(cb.build())
    val tf = new TwitterFactory(cb.build())
    val twitter = tf.getInstance()

    val statuses = twitter.getHomeTimeline

    println(statuses.size())


  }
}
