package com.naughtyzombie.thinkdvr.util;

import com.naughtyzombie.thinkdvr.OldMain;
import twitter4j.auth.AccessToken;

import java.io.*;

/**
 * Created by pattale on 22/10/2014.
 */
public class FileUtil {
    public static void writeObject(AccessToken accessToken) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(OldMain.TWITTER_ACCESS_TOKEN);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        System.out.println("Serialising:...");
        System.out.println("Access Token ID: " + accessToken.getUserId());
        System.out.println("Access Token: " + accessToken.toString());
        System.out.println("Twitter User Name: " + accessToken.getScreenName());
        out.writeObject(accessToken);
        out.close();
        fileOut.close();
    }

    public static AccessToken readAccessToken() {
        AccessToken accessToken = null;
        try {
            File file = new File(OldMain.TWITTER_ACCESS_TOKEN);
            if (file.exists()) {
                FileInputStream fileIn = new FileInputStream(OldMain.TWITTER_ACCESS_TOKEN);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                accessToken = (AccessToken) in.readObject();
                in.close();
                fileIn.close();
            }
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println(" DFTB class not found");
            c.printStackTrace();
            return null;
        }
        return accessToken;
    }
}
