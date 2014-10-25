package com.naughtyzombie.thinkdvr;

import com.naughtyzombie.thinkdvr.model.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.controlsfx.dialog.CommandLinksDialog;
import org.controlsfx.dialog.Dialogs;
import org.controlsfx.dialog.LoginDialog;

/**
 * Created by pram on 21/10/2014.
 */
public class MessageOverviewController {
    @FXML
    private TableView<Message> messageTable;
    @FXML
    private TableColumn<Message, String> screenNameColumn;
    @FXML
    private TableColumn<Message, String> textColumn;

    @FXML
    private Label screenNameLabel;

    @FXML
    private Label textLabel;


    private Main main;

    public MessageOverviewController() {
    }

    @FXML
    private void initialize() {
        screenNameColumn.setCellValueFactory(cellData -> cellData.getValue().getScreenNameProperty());
        textColumn.setCellValueFactory(cellData -> cellData.getValue().getTextProperty());

        showMessageDetails(null);

        messageTable.getSelectionModel().selectedItemProperty().addListener((
                        observable, oldValue, newValue) -> showMessageDetails(newValue)
        );
    }

    public void setMain(Main main) {
        this.main = main;
        messageTable.setItems(main.getMessageData());
    }

    private void showMessageDetails(Message message) {
        if (message != null) {
            screenNameLabel.setText(message.getScreenName());
            textLabel.setText(message.getText());
        } else {
            screenNameLabel.setText("");
            textLabel.setText("");
        }
    }

    @FXML
    private void handleDeleteMessage() {
        int selectedIndex = messageTable.getSelectionModel().getSelectedIndex();
        messageTable.getItems().remove(selectedIndex);
    }

    @FXML
    private void handleDialog() {
        Dialogs.create()
                .title("Test Dialog")
                .masthead("Warning")
                .message("Proper Message")
                .showConfirm();
    }

}
