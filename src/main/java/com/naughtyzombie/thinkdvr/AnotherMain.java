package com.naughtyzombie.thinkdvr;

import com.naughtyzombie.thinkdvr.control.ThinkDVRPopover;
import com.naughtyzombie.thinkdvr.control.ThinkDVRToolbar;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by pattale on 04/11/2014.
 */
public class AnotherMain extends Application {

    private Scene scene;
    private Pane root;
    private ThinkDVRToolbar toolBar;
    private Label titleLabel;
    private ToggleButton listButton;
    private ThinkDVRPopover popover;

    @Override
    public void init() throws Exception {
        root = new Pane() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                final double w = getWidth();
                final double h = getHeight();
                popover.autosize();
                Point2D listBtnBottomCenter = listButton.localToScene(listButton.getWidth()/2, listButton.getHeight());
                popover.setLayoutX((int)listBtnBottomCenter.getX()-50);
                popover.setLayoutY((int)listBtnBottomCenter.getY()+20);
                final double toolBarHeight = toolBar.prefHeight(w);

            }
        };

        toolBar = new ThinkDVRToolbar();
        root.getChildren().add(toolBar);

        titleLabel = new Label("ThinkDVR");
        HBox.setMargin(titleLabel, new Insets(0, 0, 0, 7));

        listButton = new ToggleButton();
        HBox.setMargin(listButton, new Insets(0, 0, 0, 7));

        popover = new ThinkDVRPopover();
        popover.setPrefWidth(440);
        root.getChildren().add(popover);

        final LoginPopover rootPage = new LoginPopover();

        listButton.setOnMouseClicked((MouseEvent e) -> {
            if (popover.isVisible()) {
                popover.hide();
            } else {
                popover.clearPages();
                popover.pushPage(rootPage);
                popover.show(() -> listButton.setSelected(false));
            }
        });

        toolBar.addLeftItems(titleLabel,listButton);
    }

    private void setStylesheets() {
        final String EXTERNAL_STYLESHEET = "http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600";
        scene.getStylesheets().setAll("/thinkdvr/ThinkDVRStylesCommon.css");
        Thread backgroundThread = new Thread(() -> {
            try {
                URL url = new URL(EXTERNAL_STYLESHEET);
                try (
                        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                        Reader newReader = Channels.newReader(rbc, "ISO-8859-1");
                        BufferedReader bufferedReader = new BufferedReader(newReader)
                ) {
                    // Checking whether we can read a line from this url
                    // without exception
                    bufferedReader.readLine();
                }
                Platform.runLater(() -> {
                    scene.getStylesheets().add(EXTERNAL_STYLESHEET);
                });
            }catch (MalformedURLException ex) {
                Logger.getLogger(AnotherMain.class.getName()).log(Level.FINE, "Failed to load external stylesheet", ex);
            }catch (IOException ex) {
                Logger.getLogger(AnotherMain.class.getName()).log(Level.FINE, "Failed to load external stylesheet", ex);
            }
        }, "Trying to reach external styleshet");
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        scene = new Scene(root, 1024, 768, Color.BLACK);
        setStylesheets();
        stage.setScene(scene);
        stage.setTitle("ThinkDVR");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
