package com.naughtyzombie.thinkdvr;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjustBuilder;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApp extends Application {

    private LoginService loginService;

    public static void main(final String[] args) {
        try {
            launch(MainApp.class, args);
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final VBox rootNode = new VBox();
        rootNode.setAlignment(Pos.CENTER);
        final Button btn = new Button("Launch Login Dialog Window");
        btn.setOnMouseClicked(event -> {
            //if (loginService != null) {
            //    loginService.hide();
            //}
            //loginService = createLoginDialog(primaryStage);
            //loginService.start();
            showLoginScreen("http://news.bbc.co.uk", primaryStage);
        });
        rootNode.getChildren().add(btn);
        primaryStage.setTitle("ThinkDVR");
        primaryStage.setScene(new Scene(rootNode, 800, 500, Color.BLACK));
        primaryStage.getScene().getStylesheets().add(MainApp.class.getResource("/view/Dialog.css").toExternalForm());
        primaryStage.show();
    }

    public void showLoginScreen(String url, Stage primaryStage) {
        //Parent root;

        try {
            /*FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginScreen.fxml"));
            //Stage stage = new Stage(StageStyle.DECORATED);
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Login to Twitter");
            LoginScreenController controller = loader.<LoginScreenController>getController();
            controller.initData(url);*/

            /*root = FXMLLoader.load(getClass().getClassLoader().getResource("/fxml/LoginScreen.fxml"));
            Stage stage = new Stage();
            stage.setTitle("My New Stage Title");
            stage.setScene(new Scene(root, 450, 450));
            stage.show();*/

            //Parent root=  FXMLLoader.load(getClass().getResource("/fxml/LoginScreen.fxml"));
            //ServerConfigChooser controller = new ServerConfigChooser();
            LoginScreenController controller = new LoginScreenController();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LoginScreen.fxml"));
            Parent root = fxmlLoader.load();
            fxmlLoader.setRoot(root);

            //root.setController(controller);
            //loader.setRoot(controller);
            //Parent root;
            //try {
            //root = loader.load();
            Scene scene = new Scene(root, 600, 400);
            Stage stage = new Stage();

            stage.setScene(scene);
            stage.show();

            //controller.initData(url);
            /*} catch (IOException ex) {
                Logger.getLogger(ServerConfigChooser.class.getName()).log(Level.SEVERE, null, ex);
            }*/

            //primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LoginService createLoginDialog(final Stage primaryStage) {
        final TextField username = TextFieldBuilder.create().promptText("Username").build();
        final PasswordField password = PasswordFieldBuilder.create().promptText("Password").build();
        final Button closeBtn = ButtonBuilder.create().text("Close").build();
        final Service<Void> submitService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        final boolean hasUsername = !username.getText().isEmpty();
                        final boolean hasPassword = !password.getText().isEmpty();
                        if (hasUsername && hasPassword) {
                            // TODO : perform some sort of authentication here
                            // or you can throw an exception to see the error
                            // message in the dialog window
                        } else {
                            final String invalidFields = (!hasUsername ? username
                                    .getPromptText() : "")
                                    + ' '
                                    + (!hasPassword ? password.getPromptText()
                                    : "");
                            throw new RuntimeException("Invalid "
                                    + invalidFields);
                        }
                        return null;
                    }
                };
            }
        };

        final LoginService loginService = dialog(primaryStage,
                "Login to Twitter",
                "Please provide a Twitter Username and Password",
                null, "Login", 550d, 300d, submitService, closeBtn, username, password);
        if (closeBtn != null) {
            closeBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent event) {
                    loginService.hide();
                }
            });
        }
        return loginService;
    }

    public static LoginService dialog(final Stage parent, final String title,
                                      final String headerText, final Image icon, final String submitLabel,
                                      final double width, final double height, final Service<Void> submitService,
                                      final Node... children) {
        final Stage window = new Stage();
        final Text header = TextBuilder.create().text(headerText).styleClass(
                "dialog-title").wrappingWidth(width / 1.2d).build();
        final Text messageHeader = TextBuilder.create().styleClass("dialog-message"
        ).wrappingWidth(width / 1.2d).build();
        final LoginService service = new LoginService(parent, window,
                messageHeader, submitService);
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.TRANSPARENT);
        if (icon != null) {
            window.getIcons().add(icon);
        }
        if (title != null) {
            window.setTitle(title);
        }
        final VBox content = VBoxBuilder.create().styleClass("dialog").build();
        content.setMaxSize(width, height);
        window.setScene(new Scene(content, width, height, Color.TRANSPARENT));
        if (parent != null) {
            window.getScene().getStylesheets().setAll(parent.getScene().getStylesheets());
        }
        final Button submitBtn = ButtonBuilder.create().text(submitLabel).defaultButton(
                true).onAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent actionEvent) {
                submitService.restart();
            }
        }).build();
        final FlowPane flowPane = new FlowPane();
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setVgap(20d);
        flowPane.setHgap(10d);
        flowPane.setPrefWrapLength(width);
        flowPane.getChildren().add(submitBtn);
        content.getChildren().addAll(header, messageHeader);
        if (children != null && children.length > 0) {
            for (final Node node : children) {
                if (node == null) {
                    continue;
                }
                if (node instanceof Button) {
                    flowPane.getChildren().add(node);
                } else {
                    content.getChildren().add(node);
                }
            }
        }
        content.getChildren().addAll(flowPane);
        return service;
    }

    public static class LoginService extends Service<Void> {

        private final Stage window;
        private final Stage parent;
        private final Effect origEffect;
        private final Service<Void> submitService;

        protected LoginService(final Stage parent, final Stage window,
                               final Text messageHeader, final Service<Void> submitService) {
            this.window = window;
            this.parent = parent;
            this.origEffect = hasParentSceneRoot() ? this.parent.getScene(
            ).getRoot().getEffect() : null;
            this.submitService = submitService;
            this.submitService.stateProperty().addListener(new ChangeListener<State>() {
                @Override
                public void changed(final ObservableValue<? extends State> observable,
                                    final State oldValue, final State newValue) {
                    if (submitService.getException() != null) {
                        // service indicated that an error occurred
                        messageHeader.setText(submitService.getException().getMessage());
                    } else if (newValue == State.SUCCEEDED) {
                        window.getScene().getRoot().setEffect(
                                ColorAdjustBuilder.create().brightness(-0.5d).build());
                        Platform.runLater(createHideTask());
                    }
                }
            });
        }

        @Override
        protected Task<Void> createTask() {
            return window.isShowing() ? createHideTask() : createShowTask();
        }

        protected Task<Void> createShowTask() {
            final Task<Void> showTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            if (hasParentSceneRoot()) {
                                parent.getScene().getRoot().setEffect(
                                        ColorAdjustBuilder.create().brightness(-0.5d).build());
                            }
                            window.show();
                            window.centerOnScreen();
                        }
                    });
                    return null;
                }
            };
            showTask.stateProperty().addListener(new ChangeListener<State>() {
                @Override
                public void changed(final ObservableValue<? extends State> observable,
                                    final State oldValue, final State newValue) {
                    if (newValue == State.FAILED || newValue == State.CANCELLED) {
                        Platform.runLater(createHideTask());
                    }
                }
            });
            return showTask;
        }

        protected Task<Void> createHideTask() {
            final Task<Void> closeTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    window.hide();
                    if (hasParentSceneRoot()) {
                        parent.getScene().getRoot().setEffect(origEffect);
                    }
                    window.getScene().getRoot().setDisable(false);
                    return null;
                }
            };
            return closeTask;
        }

        private boolean hasParentSceneRoot() {
            return this.parent != null && this.parent.getScene() != null
                    && this.parent.getScene().getRoot() != null;
        }

        public void hide() {
            Platform.runLater(createHideTask());
        }
    }
}