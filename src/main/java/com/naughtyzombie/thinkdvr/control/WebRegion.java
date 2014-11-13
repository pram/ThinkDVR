package com.naughtyzombie.thinkdvr.control;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;

import java.util.Set;

public class WebRegion extends Region {

    final WebView webView = new WebView();
    final WebEngine webEngine = webView.getEngine();
    //private WebView webView;
    //private WebEngine webEngine;

     
    public WebRegion( String content) {
        //this.webView = webView;
        //webEngine = webView.getEngine();

        this.webView.setPrefHeight(5);
    	
        widthProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                Double width = (Double)newValue;
                WebRegion.this.webView.setPrefWidth(width);
                adjustHeight();
            }    
        });

        this.webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> arg0, Worker.State oldState, Worker.State newState)         {
                if (newState == Worker.State.SUCCEEDED) {
                    adjustHeight();
                }				
            }
        });        
        
        this.webView.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> change) {
                Set<Node> scrolls = WebRegion.this.webView.lookupAll(".scroll-bar");
                for (Node scroll : scrolls) {
                   scroll.setVisible(false);
                }
            }
    	});
        
        setContent(content);
        getChildren().add(this.webView);
    }
    
    public void setContent(final String content) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webEngine.loadContent(getHtml(content));
                Platform.runLater(() -> adjustHeight());
            }
        });
    }
    
 
    @Override 
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(webView,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
    
    private void adjustHeight() {
        Platform.runLater(() -> {
            try {
                Object result = webEngine.executeScript("document.getElementById('mydiv').offsetHeight");
                if (result instanceof Integer) {
                    Integer i = (Integer) result;
                    double height = new Double(i);
                    height = height + 20;
                    webView.setPrefHeight(height);
                    webView.getPrefHeight();
                }
            } catch (JSException e) { }
        });
    }
    
    private String getHtml(String content) {
        return "<html><body><div id=\"mydiv\">" + content + "</div></body></html>";
    }

}