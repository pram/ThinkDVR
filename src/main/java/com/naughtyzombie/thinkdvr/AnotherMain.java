package com.naughtyzombie.thinkdvr;

import com.naughtyzombie.thinkdvr.control.ThinkDVRToolbar;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by pattale on 04/11/2014.
 */
public class AnotherMain extends Application {

    private Scene scene;
    private Pane root;
    private ThinkDVRToolbar toolBar;
    private ToggleButton listButton;

    @Override
    public void init() throws Exception {
        root = new Pane() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                final double w = getWidth();
                final double h = getHeight();
                final double toolBarHeight = toolBar.prefHeight(w);

            }
        };
    }

    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(root, 1024, 768, Color.BLACK);

        stage.setScene(scene);
        stage.setTitle("ThinkDVR");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
