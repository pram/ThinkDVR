package com.naughtyzombie.thinkdvr.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by pattale on 22/10/2014.
 */
public class Message {

    private final StringProperty screenName;
    private final StringProperty text;

    public Message() {
        this(null,null);
    }

    public Message(String screenName, String text) {
        this.screenName = new SimpleStringProperty(screenName);
        this.text = new SimpleStringProperty(text);
    }

    public String getScreenName() {
        return this.screenName.get();
    }

    public void setScreenName(String screenName) {
        this.screenName.set(screenName);
    }

    public StringProperty getScreenNameProperty() {
        return this.screenName;
    }

    public String getText() {
        return this.text.get();
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public StringProperty getTextProperty() {
        return this.text;
    }

}
