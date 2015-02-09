package com.naughtyzombie.thinkdvr;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;
import org.vertx.java.platform.Verticle;


/**
 * Created by pattale on 22/12/2014.
 */
public class Server extends Verticle {
    @Override
    public void start() {
        container.deployVerticle("app.js");
    }
}
