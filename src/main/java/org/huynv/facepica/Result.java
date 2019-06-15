package org.huynv.facepica;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.FaceDetail;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Result extends JFrame {
    static int scale = 1;

    private JPanel mainpanel;
    private JButton confirmbtn;
    private DisplayFaces panel;

    public Result(SearchFacesByImageResult result_search, BufferedImage image) throws Exception {

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new DisplayFaces(result_search, image);
        panel.setPreferredSize(new Dimension(image.getWidth() / scale, image.getHeight() / scale));
        setContentPane(panel);
        add(confirmbtn);
        pack();
    }

}
