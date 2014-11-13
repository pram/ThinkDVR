package com.naughtyzombie.thinkdvr;

import com.guigarage.login.ui.LoginWithFacebookButton;
import com.guigarage.login.ui.LoginWithTwitterButton;
import com.guigarage.ui.property.PropertySuppliers;
import io.datafx.core.ExceptionHandler;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginDemo extends Application {

    private static String key;
    private static String secret;

    public static void main(String... args) {
        key = args[0];
        secret = args[1];
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        PasswordField keyField = new PasswordField();
        keyField.setText(key);
        keyField.setPromptText("key");
        PasswordField secretField = new PasswordField();
        secretField.setPromptText("secret");
        secretField.setText(secret);
        TextField callbackField = new TextField();
        callbackField.setPromptText("callback URL");
        Label resultLabel = new Label();

        ExceptionHandler exceptionHandler = new ExceptionHandler();
        //exceptionHandler.exceptionProperty().addListener(e ->
        //        Dialogs.create().style(DialogStyle.NATIVE).showException(exceptionHandler.getException()));

        LoginWithFacebookButton facebookButton = new LoginWithFacebookButton(PropertySuppliers.create(keyField.textProperty()),
                PropertySuppliers.create(secretField.textProperty()),
                PropertySuppliers.create(callbackField.textProperty()),
                user -> resultLabel.setText("Welcome " + user.getFirstName() + " " + user.getLastName()),
                exceptionHandler);


        LoginWithTwitterButton twitterButton = new LoginWithTwitterButton(PropertySuppliers.create(keyField.textProperty()),
                PropertySuppliers.create(secretField.textProperty()),
                PropertySuppliers.create(callbackField.textProperty()),
                user -> resultLabel.setText("Welcome " + user.getFirstName() + " " + user.getLastName()),
                exceptionHandler);
        twitterButton.setAlignment(Pos.CENTER);

        VBox.setMargin(facebookButton, new Insets(12, 0, 0, 0));
        VBox.setMargin(resultLabel, new Insets(12, 0, 0, 0));
        VBox box = new VBox(keyField, secretField, callbackField, facebookButton, twitterButton, resultLabel);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(12));
        box.setSpacing(6);
        primaryStage.setTitle("LoginWithFX");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(box));
        primaryStage.show();
    }
}
