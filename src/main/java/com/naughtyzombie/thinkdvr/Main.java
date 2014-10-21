package com.naughtyzombie.thinkdvr;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import com.twitter.hbc.twitter4j.handler.StatusStreamHandler;
import com.twitter.hbc.twitter4j.message.DisconnectMessage;
import com.twitter.hbc.twitter4j.message.StallWarningMessage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.concurrent.*;

/**
 * Created by pattale on 16/10/2014.
 */
public class Main extends Application {

    public static final String TWITTER_ACCESS_TOKEN = "twitter.accessToken";
    private static AccessToken accessToken;
    private static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";








    private StatusListener listener1 = new StatusListener() {
        @Override
        public void onStatus(Status status) {
            System.out.println("l1 status");
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            System.out.println("l1 onDeletionNotice");
        }

        @Override
        public void onTrackLimitationNotice(int limit) {
            System.out.println("l1 onTrackLimitationNotice");
        }

        @Override
        public void onScrubGeo(long user, long upToStatus) {
            System.out.println("l1 onScrubGeo");
        }

        @Override
        public void onStallWarning(StallWarning warning) {
            System.out.println("l1 onStallWarning");
        }

        @Override
        public void onException(Exception e) {
            System.out.println("l1 onException");
        }
    };

    // A bare bones StatusStreamHandler, which extends listener and gives some extra functionality
    private StatusListener listener2 = new StatusStreamHandler() {
        @Override
        public void onStatus(Status status) {
            System.out.println("l2 onStatus");
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            System.out.println("l2 onDeletionNotice");
        }

        @Override
        public void onTrackLimitationNotice(int limit) {
            System.out.println("l2 onTrackLimitationNotice");
        }

        @Override
        public void onScrubGeo(long user, long upToStatus) {
            System.out.println("l2 onScrubGeo");
        }

        @Override
        public void onStallWarning(StallWarning warning) {
            System.out.println("l2 onStallWarning");
        }

        @Override
        public void onException(Exception e) {
            System.out.println("l2 onException");
        }

        @Override
        public void onDisconnectMessage(DisconnectMessage message) {
            System.out.println("l2 onDisconnectMessage");
        }

        @Override
        public void onStallWarningMessage(StallWarningMessage warning) {
            System.out.println("l2 onStallWarningMessage");
        }

        @Override
        public void onUnknownMessageType(String s) {
            System.out.println("l2 onUnknownMessageType");
        }
    };

    private void testRun(String consumerKey, String consumerSecret) throws TwitterException, IOException, InterruptedException {
        // The factory instance is re-useable and thread safe.
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = readAccessToken();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            String pin = br.readLine();
            try {
                if (pin.length() > 0) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if (401 == te.getStatusCode()) {
                    System.out.println("Unable to get the access token.");
                } else {
                    te.printStackTrace();
                }
            }

            if (accessToken != null) {
                //persist to the accessToken for future reference.
                storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
            }
        }

        twitter.setOAuthAccessToken(accessToken);
        System.out.println(twitter.getScreenName());
        System.out.println("Connection Established ");

        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

        StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
        endpoint.stallWarnings(false);

        Authentication auth = new OAuth1(consumerKey, consumerSecret, accessToken.getToken(), accessToken.getTokenSecret());

        BasicClient client = new ClientBuilder()
                .name("ThinkDVR")
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        /*int numProcessingThreads = 4;
        ExecutorService service = Executors.newFixedThreadPool(numProcessingThreads);

        Twitter4jStatusClient t4jClient = new Twitter4jStatusClient(
                client, queue, Lists.newArrayList(listener1, listener2), service);

        t4jClient.connect();
        for (int threads = 0; threads < numProcessingThreads; threads++) {
            // This must be called once per processing thread
            t4jClient.process();
        }

        Thread.sleep(5000);

        client.stop();
        */

        // Establish a connection
        client.connect();

        // Do whatever needs to be done with messages
        for (int msgRead = 0; msgRead < 1000; msgRead++) {
            if (client.isDone()) {
                System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
                break;
            }

            String msg = queue.poll(5, TimeUnit.SECONDS);
            if (msg == null) {
                System.out.println("Did not receive a message in 5 seconds");
            } else {
                System.out.println(msg);
            }
        }

        client.stop();

        // Print some stats
        System.out.printf("The client read %d messages!\n", client.getStatsTracker().getNumMessages());

    }

    private void storeAccessToken(long useId, AccessToken accessToken) throws IOException {
        this.accessToken = accessToken;
        writeObject(accessToken);
    }

    public static void main(String[] args) throws Exception {
/*
 *  fix for
 *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
 *       sun.security.validator.ValidatorException:
 *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
 *               unable to find valid certification path to requested target
 */
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {

                    public boolean isClientTrusted(java.security.cert.X509Certificate[] x509Certificates) {
                        return false;
                    }


                    public boolean isServerTrusted(java.security.cert.X509Certificate[] x509Certificates) {
                        return false;
                    }

                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }

                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

// Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
// Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
/*
 * end of the fix
 */

        URL url = new URL("https://twitter.com");

        URLConnection con = url.openConnection();
        Reader reader = new InputStreamReader(con.getInputStream());
        while (true) {
            int ch = reader.read();
            if (ch == -1) {
                break;
            }
            System.out.print((char) ch);
        }

        Main main = new Main();
        //main.run2(args[0], args[1]);
        //main.testRun(args[0], args[1]);
        //main.run(args[0], args[1],args[2], args[3]);
        //main.runFilter(args[0], args[1], args[2], args[3]);

        launch(args);


    }

    public static void mainx(String[] args) throws InterruptedException, TwitterException, IOException {
        Main main = new Main();
        //main.runFilter(args[0], args[1], args[2], args[3]);
        //main.run(args[0], args[1],args[2], args[3]);
        main.testRun(args[0], args[1]);
    }

    private static void writeObject(AccessToken accessToken) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(TWITTER_ACCESS_TOKEN);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        System.out.println("Serialising:...");
        System.out.println("Access Token ID: " + accessToken.getUserId());
        System.out.println("Access Token: " + accessToken.toString());
        System.out.println("Twitter User Name: " + accessToken.getScreenName());
        out.writeObject(accessToken);
        out.close();
        fileOut.close();
    }

    private static AccessToken readAccessToken() {
        AccessToken accessToken = null;
        try {
            File file = new File(TWITTER_ACCESS_TOKEN);
            if (file.exists()) {
                FileInputStream fileIn = new FileInputStream(TWITTER_ACCESS_TOKEN);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                accessToken = (AccessToken) in.readObject();
                in.close();
                fileIn.close();
            }
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println(" DFTB class not found");
            c.printStackTrace();
            return null;
        }
        return accessToken;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setTitle("ThinkDVR");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
}
