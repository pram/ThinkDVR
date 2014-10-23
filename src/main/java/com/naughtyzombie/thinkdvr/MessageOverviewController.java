package com.naughtyzombie.thinkdvr;

import com.naughtyzombie.thinkdvr.model.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
    }

    public void setMain(Main main) {
        this.main = main;
        messageTable.setItems(main.getMessageData());
    }
}
