package com.naughtyzombie.thinkdvr;

import com.naughtyzombie.thinkdvr.util.FileUtil;
import javafx.application.Application;
import javafx.stage.Stage;
import twitter4j.auth.AccessToken;

/**
 * Created by pram on 27/10/2014.
 */
public class NewMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AccessToken accessToken = FileUtil.readAccessToken();
        if (accessToken == null) {

        }

    }
}
