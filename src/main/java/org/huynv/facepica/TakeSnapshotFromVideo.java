package org.huynv.facepica;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;


@SuppressWarnings("serial")
public class TakeSnapshotFromVideo extends JFrame {

    private class SnapMeAction extends AbstractAction {

        public SnapMeAction() {
            super("Snapshot");
        }

        
        public void actionPerformed(ActionEvent e) {
            try {
                for (int i = 0; i < webcams.size(); i++) {
                    Webcam webcam = webcams.get(i);
                    File file = new File("data/current/" + String.format("chechIn-%d.jpg", i));
                    ImageIO.write(webcam.getImage(), "JPG", file);
                    System.out.format("Image for %s saved in %s \n", webcam.getName(), file);

                    S3.uploadObjectToBucket(file.getAbsolutePath(), bucket);
                    System.out.println("Uploaded to bucket");
                    System.out.println("=================================================");

                    checkIn(file.getName());
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class StartAction extends AbstractAction implements Runnable {

        public StartAction() {
            super("Start");
        }

        
        public void actionPerformed(ActionEvent e) {

            btStart.setEnabled(false);
            btSnapMe.setEnabled(true);

            // remember to start panel asynchronously - otherwise GUI will be
            // blocked while OS is opening webcam HW (will have to wait for
            // webcam to be ready) and this causes GUI to hang, stop responding
            // and repainting

            executor.execute(this);
        }

        
        public void run() {

            btStop.setEnabled(true);

            for (WebcamPanel panel : panels) {
                panel.start();
            }
        }
    }

    private class StopAction extends AbstractAction {

        public StopAction() {
            super("Stop");
        }

        
        public void actionPerformed(ActionEvent e) {

            btStart.setEnabled(true);
            btSnapMe.setEnabled(false);
            btStop.setEnabled(false);

            for (WebcamPanel panel : panels) {
                panel.stop();
            }
        }
    }

    private Executor executor = Executors.newSingleThreadExecutor();

    private Dimension size = WebcamResolution.VGA.getSize();

    private List<Webcam> webcams = Webcam.getWebcams();
    private List<WebcamPanel> panels = new ArrayList<WebcamPanel>();

    private JButton btSnapMe = new JButton(new SnapMeAction());
    private JButton btStart = new JButton(new StartAction());
    private JButton btStop = new JButton(new StopAction());

    private String collection;
    private String bucket;

    public TakeSnapshotFromVideo(String collection_name, String bucket_name) {

        super("Test Snap Different Size");

        for (Webcam webcam : webcams) {
            webcam.setViewSize(size);
            WebcamPanel panel = new WebcamPanel(webcam, size, true);
            panel.setFPSDisplayed(true);
            panel.setFillArea(true);
            panels.add(panel);
        }

        this.collection = collection_name;
        this.bucket = bucket_name;

        // start application with disable snapshot button - we enable it when
        // webcam is started

        btSnapMe.setEnabled(false);
        btStop.setEnabled(false);

        setLayout(new FlowLayout());

        for (WebcamPanel panel : panels) {
            add(panel);
        }

        add(btSnapMe);
        add(btStart);
        add(btStop);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Topican checkIn(String image_name) throws Exception {
        // Search image in collection
        SearchFacesByImageResult result_search = new SearchFacesByImageResult();
        try {
            result_search = Collection.searchFaceMatchingImageCollection(this.collection, this.bucket, image_name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        List<FaceMatch> list_faces = result_search.getFaceMatches();

        if (list_faces.size() > 0) {
            // get info from server and show confirm
            AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();

            com.amazonaws.services.s3.model.S3Object s3object = s3client.getObject(bucket, image_name);
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            BufferedImage image = ImageIO.read(inputStream);

            new Result(result_search, image);
        } else {
            // Show input require
        }

        return new Topican(0,"test", "t");
        // If match sent id face to server, get info Topican and return the topican

        // Else Show require input
    }
}