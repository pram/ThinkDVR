package com.naughtyzombie.thinkdvr;

import com.naughtyzombie.thinkdvr.control.ThinkDVRPopover;
import com.naughtyzombie.thinkdvr.control.ThinkDVRToolbar;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
                final double toolBarHeight = toolBar.prefHeight(w);

            }
        };

        toolBar = new ThinkDVRToolbar();
        root.getChildren().add(toolBar);

        listButton = new ToggleButton();
        HBox.setMargin(listButton, new Insets(0, 0, 0, 7));

        popover = new ThinkDVRPopover();
        popover.setPrefWidth(440);
        root.getChildren().add(popover);

        listButton.setOnMouseClicked((MouseEvent e) -> {
            if (popover.isVisible()) {
                popover.hide();
            } else {
                popover.clearPages();
                //popover.pushPage(rootPage);
                popover.show(() -> listButton.setSelected(false));
            }
        });

        toolBar.addLeftItems(listButton);
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
