package org.huynv.facepica;

import com.google.gson.Gson;
import javax.swing.*;
import java.io.FileInputStream;
import java.util.Properties;

public class App 
{
    private String bucket_name;
    private String collection_name;
    private String path;
    private Properties config;
    private Gson gson;

    public App() {
        this.path = System.getProperty("user.dir");
        this.config = new Properties();

        try {
            this.config.load(new FileInputStream(this.path + "/config.properties"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        this.bucket_name = this.config.getProperty("BUCKET");
        this.collection_name = this.config.getProperty("COLLECTION");
        this.gson = new Gson();
    }

    public void start() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        //new TakeSnapshotFromVideo(collection_name, bucket_name);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                WebcamCheckin test = new WebcamCheckin(collection_name, bucket_name);
                try {
//                    Result test = new Result();
                    test.setVisible(true);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

    }

    public static void main( String[] args ) throws Exception
    {
        new App().start();

    }
}
