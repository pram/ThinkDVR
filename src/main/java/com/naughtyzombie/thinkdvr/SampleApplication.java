package com.naughtyzombie.thinkdvr;

import com.naughtyzombie.thinkdvr.control.ExampleControl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SampleApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new ExampleControl(), 300, 250);
        
        primaryStage.setTitle("Custom CustomControl sample");
        primaryStage.setScene(scene);
        
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}