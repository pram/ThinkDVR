package com.naughtyzombie.thinkdvr;

import com.naughtyzombie.thinkdvr.control.ThinkDVRPopover;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.web.WebView;

/**
 * Created by pattale on 04/11/2014.
 */
public class LoginPopover extends Control implements ThinkDVRPopover.Page {

    private ThinkDVRPopover popover;
    private WebView webView;

    @Override
    public void setPopover(ThinkDVRPopover popover) {
        this.popover = popover;
    }

    @Override
    public ThinkDVRPopover getPopover() {
        return this.popover;
    }

    @Override
    public Node getPageNode() {
        return this;
    }

    @Override
    public String getPageTitle() {
        return "Woot!";
    }

    @Override
    public String leftButtonText() {
        return "ggggg";
    }

    @Override
    public void handleLeftButton() {

    }

    @Override
    public String rightButtonText() {
        return "jjjjjj";
    }

    @Override
    public void handleRightButton() {

    }

    @Override
    public void handleShown() {

    }

    @Override
    public void handleHidden() {

    }

    @Override protected double computePrefWidth(double height) {
        return 200;
    }

    @Override protected double computePrefHeight(double width) {
        return 88;
    }
}
