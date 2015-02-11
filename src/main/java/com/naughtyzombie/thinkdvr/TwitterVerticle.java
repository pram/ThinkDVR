package com.naughtyzombie.thinkdvr;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.Authorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.vertx.java.platform.Verticle;

public class TwitterVerticle extends Verticle {

	public static final String TWITTER_ACCESS_TOKEN = "twitter.accessToken";
    private static AccessToken accessToken;
    private static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";

}