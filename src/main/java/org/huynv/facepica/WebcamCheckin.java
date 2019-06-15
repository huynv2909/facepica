package org.huynv.facepica;

import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebcamCheckin extends JFrame {

    private class Checkin extends AbstractAction {

        public Checkin() {
            super("Checkin");
        }

        public void actionPerformed(ActionEvent e) {
            long startTime = System.nanoTime();
            counter++;
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                Date date = new Date();
                String name = dateFormat.format(date);

                Webcam webcam = webcams.get(0);
                File file = new File("data/current/" + String.format(name + "_%d.jpg", counter));
                ImageIO.write(webcam.getImage(), "JPG", file);
                img_name = file.getName();

//
                S3.uploadObjectToBucket(file.getAbsolutePath(), bucket);
                long endTime   = System.nanoTime();
                System.out.println(endTime - startTime);
                startTime = System.nanoTime();

                SearchFacesByImageResult result_search = Collection.searchFaceMatchingImageCollection(collection, bucket, file.getName());
                endTime   = System.nanoTime();
                System.out.println(endTime - startTime);

                BufferedImage image = ImageIO.read(new File(file.getAbsolutePath()));
                showImg = new DisplayFaces(result_search, image);
                showImg.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
                showImg.setLayout(null);

                if (result_search.getFaceMatches().size() > 0) {
                    attender = Connector.getTopicanByIdFace(result_search.getFaceMatches().get(0).getFace().getFaceId());
                    info = new JLabel("You are " + attender.getAccount() + " ?");
                    info.setOpaque(true);
                    info.setBackground(new Color(255,255,255));
                    info.setBounds(size.width/2 - 100,size.height - 80,200,30);
                    showImg.add(info);

                    confirm.setBounds(size.width/2 - 50,size.height - 40,100,30);
                    showImg.add(confirm);
                }


                webcamPanel.setVisible(false);
                add(showImg);
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class Confirm extends AbstractAction {
        public Confirm() {
            super("OK");
        }

        public void actionPerformed(ActionEvent e) {
            // Update to database and show cam
            Connector.attendance(attender.getId(), img_name);

            showImg.setVisible(false);
            webcamPanel.setVisible(true);
        }
    }

    private WebcamPanel webcamPanel;
    private JButton checkin = new JButton(new Checkin());
    private JButton checkout = new JButton(new Checkin());
    private JButton confirm = new JButton(new Confirm());
    private JLabel info;

    private JPanel rootPanel;

    private DisplayFaces showImg;

    private Dimension size = WebcamResolution.VGA.getSize();

    private java.util.List<Webcam> webcams = Webcam.getWebcams();

    private String collection;
    private String bucket;
    private int counter;

    private Topican attender;
    private String img_name;

    public WebcamCheckin(String collection_name, String bucket_name) {

        super("Facepica");

        Webcam webcam = webcams.get(0);
        webcam.setViewSize(size);
        this.webcamPanel = new WebcamPanel(webcam, size, true);
        this.webcamPanel.setFPSDisplayed(true);
        this.webcamPanel.setFillArea(true);

        this.collection = collection_name;
        this.bucket = bucket_name;
        this.counter = 0;

        setLayout(new FlowLayout());

        this.webcamPanel.setLayout(null);
        add(this.webcamPanel);

        checkin.setBounds(size.width/2 - 110,size.height - 40,100,30);
        checkout.setBounds(size.width/2 + 10,size.height - 40,100,30);
        checkout.setText("Check Out");
        this.webcamPanel.add(checkin);
        this.webcamPanel.add(checkout);

        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}
