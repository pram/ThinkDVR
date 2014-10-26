package com.naughtyzombie.thinkdvr;

import com.naughtyzombie.thinkdvr.model.Message;
import com.naughtyzombie.thinkdvr.util.FileUtil;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import twitter4j.*;
import twitter4j.auth.AccessToken;
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

/**
 * Created by pattale on 16/10/2014.
 */
public class Main extends Application {

    public static final String TWITTER_ACCESS_TOKEN = "twitter.accessToken";
    private static AccessToken accessToken;
    private static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";
    private ObservableList<Message> messageData = FXCollections.observableArrayList();

    //JavaFX Stuff
    private Stage primaryStage;
    private BorderPane rootLayout;

    private void testRun(String consumerKey, String consumerSecret) throws TwitterException, IOException, InterruptedException {
        // The factory instance is re-useable and thread safe.
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = FileUtil.readAccessToken();
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
        /*client.connect();

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

        client.stop();*/

        // Print some stats
        //System.out.printf("The client read %d messages!\n", client.getStatsTracker().getNumMessages());

        //client.connect();

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey(consumerKey);
        cb.setOAuthConsumerSecret(consumerSecret);
        cb.setOAuthAccessToken(accessToken.getToken());
        cb.setOAuthAccessTokenSecret(accessToken.getTokenSecret());


        //TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

        twitterStream.addListener(listener);
        twitterStream.sample();

    }

    private void storeAccessToken(long useId, AccessToken accessToken) throws IOException {
        this.accessToken = accessToken;
        FileUtil.writeObject(accessToken);
    }

    public static void main(String[] args) throws Exception {

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
        //main.testRun(args[0], args[1]);


        launch(args);


    }


    public Main() {
        messageData.add(new Message("wiigle", "jiggle"));
        messageData.add(new Message("gooop", "ttttttt"));
    }

    private void sampleData() {

    }

    public ObservableList<Message> getMessageData() {
        return this.messageData;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ThinkDVR");

        initRootLayout();

        showLoginScreen();

        //showMessageOverview();
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/fxml/Main.fxml"));
            this.rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            this.primaryStage.setScene(scene);
            this.primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showMessageOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/fxml/MessageOverview.fxml"));
            AnchorPane messageOverview = loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(messageOverview);

            MessageOverviewController controller = loader.getController();
            controller.setMain(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginScreen() {
        try {
            // Load person overview.
            /*FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/fxml/LoginScreen.fxml"));
            AnchorPane loginScreen = loader.load();

            // Set person overview into the center of root layout.
            //rootLayout.setCenter(messageOverview);

            LoginScreenController controller = loader.getController();
            //controller.setMain(this);*/

            /*Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml"));
            primaryStage.setTitle("WebViewSampel");
            primaryStage.setScene(new Scene(root, 300, 400));
            primaryStage.show();*/

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginScreen.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(new Scene(loader.load()));
            LoginScreenController controller = loader.<LoginScreenController>getController();
            controller.initData("http://www.eurogamer.net");

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    StatusListener listener = new StatusListener() {
        @Override
        public void onStatus(Status status) {
            System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {
            System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
        }

        @Override
        public void onStallWarning(StallWarning warning) {
            System.out.println("Got stall warning:" + warning);
        }

        @Override
        public void onException(Exception ex) {
            ex.printStackTrace();
        }
    };




    /* Sample Messages
    {"created_at":"Wed Oct 22 08:07:26 +0000 2014","id":524834400404791296,"id_str":"524834400404791296","text":"because I can control them and change them.","source":"\u003ca href=\"http:\/\/twitter.com\" rel=\"nofollow\"\u003eTwitter Web Client\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":2168871000,"id_str":"2168871000","name":"BillieJean","screen_name":"setonfrequency","location":"","url":null,"description":"Just Art","protected":false,"verified":false,"followers_count":29,"friends_count":10,"listed_count":1,"favourites_count":351,"statuses_count":658,"created_at":"Fri Nov 01 18:16:56 +0000 2013","utc_offset":7200,"time_zone":"Ljubljana","geo_enabled":false,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"C0DEED","profile_background_image_url":"http:\/\/pbs.twimg.com\/profile_background_images\/493692259238293505\/SBhxhB2P.jpeg","profile_background_image_url_https":"https:\/\/pbs.twimg.com\/profile_background_images\/493692259238293505\/SBhxhB2P.jpeg","profile_background_tile":true,"profile_link_color":"0084B4","profile_sidebar_border_color":"FFFFFF","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":false,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/491844443838767105\/cddG1He-_normal.jpeg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/491844443838767105\/cddG1He-_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/2168871000\/1412791191","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"trends":[],"urls":[],"user_mentions":[],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"medium","lang":"en","timestamp_ms":"1413965246669"}

{"created_at":"Wed Oct 22 08:07:26 +0000 2014","id":524834400392216576,"id_str":"524834400392216576","text":"http:\/\/t.co\/Nc6JG0tLAG http:\/\/t.co\/KH5Gpc483T http:\/\/t.co\/ucmT3sdRJU \u0412 \u043d\u0430\u0448\u0435 \u0432\u0440\u0435\u043c\u044f \u0442\u0443\u0440\u0438\u0441\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0435","source":"\u003ca href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\"\u003eTwitter for iPhone\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":2285066792,"id_str":"2285066792","name":"\u0412\u0435\u043b\u0438\u0447\u043a\u043e \u041c\u0430\u0440\u044c\u044f\u043d\u0430","screen_name":"MarianaVell","location":"","url":null,"description":null,"protected":false,"verified":false,"followers_count":0,"friends_count":0,"listed_count":0,"favourites_count":0,"statuses_count":2185,"created_at":"Fri Jan 10 13:04:47 +0000 2014","utc_offset":null,"time_zone":null,"geo_enabled":false,"lang":"ru","contributors_enabled":false,"is_translator":false,"profile_background_color":"C0DEED","profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_tile":false,"profile_link_color":"0084B4","profile_sidebar_border_color":"C0DEED","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":true,"profile_image_url":"http:\/\/abs.twimg.com\/sticky\/default_profile_images\/default_profile_1_normal.png","profile_image_url_https":"https:\/\/abs.twimg.com\/sticky\/default_profile_images\/default_profile_1_normal.png","default_profile":true,"default_profile_image":true,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"trends":[],"urls":[{"url":"http:\/\/t.co\/Nc6JG0tLAG","expanded_url":"http:\/\/p1utin15710.org\/MNHAFHYSBV-11063219","display_url":"p1utin15710.org\/MNHAFHYSBV-110\u2026","indices":[0,22]},{"url":"http:\/\/t.co\/KH5Gpc483T","expanded_url":"http:\/\/p1utin15710.org\/YTJJIQMHQD-10561932","display_url":"p1utin15710.org\/YTJJIQMHQD-105\u2026","indices":[23,45]},{"url":"http:\/\/t.co\/ucmT3sdRJU","expanded_url":"http:\/\/p1utin15710.org\/XAUMVJLKLQ-11052124","display_url":"p1utin15710.org\/XAUMVJLKLQ-110\u2026","indices":[46,68]}],"user_mentions":[],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"medium","lang":"ru","timestamp_ms":"1413965246668"}

{"created_at":"Wed Oct 22 08:07:26 +0000 2014","id":524834400387997696,"id_str":"524834400387997696","text":"RT @JuliaHB1: Even if Leon Brittan lived in my street, I would still probably be able to come up with a good enough excuse not to invite hi\u2026","source":"\u003ca href=\"http:\/\/twitter.com\" rel=\"nofollow\"\u003eTwitter Web Client\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":1572595045,"id_str":"1572595045","name":"Curiously Hard ","screen_name":"Holborncompany","location":"Thunderbird 5","url":null,"description":"Tetchy Librarytarian, Known to be a tad facetious so I've censored my bio incase it offends you, You're fucking welcome","protected":false,"verified":false,"followers_count":1580,"friends_count":399,"listed_count":24,"favourites_count":26578,"statuses_count":41498,"created_at":"Sat Jul 06 11:24:48 +0000 2013","utc_offset":null,"time_zone":null,"geo_enabled":true,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"030303","profile_background_image_url":"http:\/\/pbs.twimg.com\/profile_background_images\/378800000087637165\/0c37c11cffb7104ac44703dab9a3ab9e.jpeg","profile_background_image_url_https":"https:\/\/pbs.twimg.com\/profile_background_images\/378800000087637165\/0c37c11cffb7104ac44703dab9a3ab9e.jpeg","profile_background_tile":true,"profile_link_color":"000000","profile_sidebar_border_color":"000000","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":true,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/521079674193989632\/VP8vsXlU_normal.jpeg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/521079674193989632\/VP8vsXlU_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/1572595045\/1377963900","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweeted_status":{"created_at":"Wed Oct 22 08:06:42 +0000 2014","id":524834214571950080,"id_str":"524834214571950080","text":"Even if Leon Brittan lived in my street, I would still probably be able to come up with a good enough excuse not to invite him dinner...","source":"\u003ca href=\"http:\/\/twitter.com\/#!\/download\/ipad\" rel=\"nofollow\"\u003eTwitter for iPad\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":459390022,"id_str":"459390022","name":"Julia Hartley-Brewer","screen_name":"JuliaHB1","location":"London","url":"http:\/\/www.lbc.co.uk","description":"LBC radio talk show presenter Mon-Fri 1pm-4pm. Ex-newspaper political editor & columnist. Married to 1st husband, mum to my gorgeous girl. All views blah blah..","protected":false,"verified":false,"followers_count":20776,"friends_count":112,"listed_count":192,"favourites_count":284,"statuses_count":15913,"created_at":"Mon Jan 09 16:23:03 +0000 2012","utc_offset":null,"time_zone":null,"geo_enabled":false,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"C0DEED","profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_tile":false,"profile_link_color":"0084B4","profile_sidebar_border_color":"C0DEED","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":true,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/417630818819784705\/h0bWTyXC_normal.jpeg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/417630818819784705\/h0bWTyXC_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/459390022\/1399998338","default_profile":true,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":1,"favorite_count":1,"entities":{"hashtags":[],"trends":[],"urls":[],"user_mentions":[],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"low","lang":"en"},"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"trends":[],"urls":[],"user_mentions":[{"screen_name":"JuliaHB1","name":"Julia Hartley-Brewer","id":459390022,"id_str":"459390022","indices":[3,12]}],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"medium","lang":"en","timestamp_ms":"1413965246683"}

{"created_at":"Wed Oct 22 08:07:26 +0000 2014","id":524834400370827265,"id_str":"524834400370827265","text":"\u0e0b\u0e2d\u0e07\u0e21\u0e34\u0e19\u0e40\u0e15\u0e47\u0e21\u0e46\u0e2d\u0e48\u0e30\u0e21\u0e36\u0e07 \u0e40\u0e40\u0e15\u0e48\u0e19\u0e32\u0e07\u0e19\u0e48\u0e32\u0e23\u0e31\u0e01\ud83d\ude02","source":"\u003ca href=\"http:\/\/twitter.com\/#!\/download\/ipad\" rel=\"nofollow\"\u003eTwitter for iPad\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":616238225,"id_str":"616238225","name":"\uc740\ud575\u2661","screen_name":"Hyuktzmaru","location":"\u0e40\u0e14\u0e2d\u0e30\u0e25\u0e32\u0e2a\u0e40\u0e40\u0e21\u0e19\u0e2a\u0e30\u0e14\u0e34\u0e49\u0e07","url":null,"description":"\u0e40\u0e2d\u0e4a\u0e14\u0e40\u0e08 #ELF99LiNE #\u0e2e\u0e2d\u0e25 \u3163B\ud300 | GOT7\u0e40\u0e25\u0e47\u0e01\u0e19\u0e49\u0e2d\u0e22\u0e01\u0e23\u0e30\u0e1b\u0e34\u0e14\u0e01\u0e23\u0e30\u0e1b\u0e2d\u0e22\u0e0a\u0e35\u0e27\u0e34\u0e15 |","protected":false,"verified":false,"followers_count":985,"friends_count":371,"listed_count":1,"favourites_count":3166,"statuses_count":141396,"created_at":"Sat Jun 23 16:10:14 +0000 2012","utc_offset":25200,"time_zone":"Bangkok","geo_enabled":false,"lang":"th","contributors_enabled":false,"is_translator":false,"profile_background_color":"FF6699","profile_background_image_url":"http:\/\/pbs.twimg.com\/profile_background_images\/378800000088343497\/d740696e2a19aecb3aa748e9db8d50a4.png","profile_background_image_url_https":"https:\/\/pbs.twimg.com\/profile_background_images\/378800000088343497\/d740696e2a19aecb3aa748e9db8d50a4.png","profile_background_tile":true,"profile_link_color":"B40B43","profile_sidebar_border_color":"FFFFFF","profile_sidebar_fill_color":"E5507E","profile_text_color":"362720","profile_use_background_image":true,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/522360798140567554\/q9LjxapZ_normal.jpeg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/522360798140567554\/q9LjxapZ_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/616238225\/1411908472","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"trends":[],"urls":[],"user_mentions":[],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"medium","lang":"th","timestamp_ms":"1413965246662"}

{"created_at":"Wed Oct 22 08:07:26 +0000 2014","id":524834400379613184,"id_str":"524834400379613184","text":"Cuppa anyone?! \ud83d\ude0c\u2615\ufe0f","source":"\u003ca href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\"\u003eTwitter for iPhone\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":2270716096,"id_str":"2270716096","name":"Tip Top Tipping","screen_name":"TrebleT_","location":"","url":null,"description":"Bets funding the weekend. Keep it simple..","protected":false,"verified":false,"followers_count":387,"friends_count":212,"listed_count":3,"favourites_count":157,"statuses_count":2699,"created_at":"Thu Jan 09 12:18:19 +0000 2014","utc_offset":3600,"time_zone":"Casablanca","geo_enabled":false,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"C0DEED","profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png","profile_background_tile":false,"profile_link_color":"0084B4","profile_sidebar_border_color":"C0DEED","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":true,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/501513524095774720\/wi3QwG0E_normal.jpeg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/501513524095774720\/wi3QwG0E_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/2270716096\/1408715328","default_profile":true,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"trends":[],"urls":[],"user_mentions":[],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"medium","lang":"en","timestamp_ms":"1413965246664"}

{"created_at":"Wed Oct 22 08:07:26 +0000 2014","id":524834400375017472,"id_str":"524834400375017472","text":"Today stats: 2 followers, 3 unfollowers and followed 2 people via http:\/\/t.co\/Ky23VXEMi8","source":"\u003ca href=\"http:\/\/unfollowers.com\" rel=\"nofollow\"\u003eUnfollowers.me\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":1447822495,"id_str":"1447822495","name":"Delajah\u2728","screen_name":"_lajahhhh","location":"iowa\u270a|C\/O 2015 WHS\u261d\ufe0f","url":null,"description":"FMOIG:_lajahhh","protected":false,"verified":false,"followers_count":774,"friends_count":855,"listed_count":0,"favourites_count":3980,"statuses_count":23254,"created_at":"Wed May 22 02:49:09 +0000 2013","utc_offset":-14400,"time_zone":"Eastern Time (US & Canada)","geo_enabled":true,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"C0DEED","profile_background_image_url":"http:\/\/pbs.twimg.com\/profile_background_images\/378800000028425055\/16031ca7c937b27ffb552906875ef352.jpeg","profile_background_image_url_https":"https:\/\/pbs.twimg.com\/profile_background_images\/378800000028425055\/16031ca7c937b27ffb552906875ef352.jpeg","profile_background_tile":true,"profile_link_color":"647980","profile_sidebar_border_color":"FFFFFF","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":true,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/511752380329627648\/yHJS8mJe_normal.jpeg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/511752380329627648\/yHJS8mJe_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/1447822495\/1407990597","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"trends":[],"urls":[{"url":"http:\/\/t.co\/Ky23VXEMi8","expanded_url":"http:\/\/uapp.ly","display_url":"uapp.ly","indices":[66,88]}],"user_mentions":[],"symbols":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"filter_level":"medium","lang":"en","timestamp_ms":"1413965246662"}

     */
}
