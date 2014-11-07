package com.naughtyzombie.thinkdvr.control;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ExampleControl extends CustomControl {
 
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private Label fullName;
     
    public ExampleControl() {
        this.fullName.textProperty().bind(
                Bindings.when(Bindings.equal(firstName.textProperty(), "")
                        .and(Bindings.equal(lastName.textProperty(), "")))
                .then("[enter first and last names]")
                .otherwise(Bindings.format(
                    "Your full name is: %s %s", 
                     firstName.textProperty(), 
                     lastName.textProperty())));
    }
}