package com.naughtyzombie.thinkdvr;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

import java.io.IOException;

/**
 * Created by pram on 06/11/2014.
 */
public class WebPane extends AnchorPane {
    @FXML
    protected WebView webView;

    public WebPane() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/WebPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
