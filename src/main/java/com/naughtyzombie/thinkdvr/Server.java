package com.naughtyzombie.thinkdvr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;
import org.vertx.java.platform.Verticle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by pattale on 22/12/2014.
 */
public class Server extends Verticle {
    @Override
    public void start() {
        container.deployVerticle("app.js");
    }
}
