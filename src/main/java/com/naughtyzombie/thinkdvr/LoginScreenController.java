package com.naughtyzombie.thinkdvr;
/**
 * Created by pram on 20/10/2014.
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class LoginScreenController {
    @FXML
    private TextField txtfield;
    @FXML
    private WebView web = new WebView();
    private WebEngine webEngine;

    @FXML
    private void OnWeb(ActionEvent event){
        webEngine = web.getEngine();
        webEngine.load(txtfield.getText());
    }

    public void initData(String url) {
        webEngine = web.getEngine();
        webEngine.load(url);
    }
}
