package org.huynv.facepica;


//Loads images, detects faces and draws bounding boxes.Determines exif orientation, if necessary.

//Import the basic graphics classes.
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;

import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

// Calls DetectFaces and displays a bounding box around each detected image.
public class DisplayFaces extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    BufferedImage image;
    static int scale;
    SearchFacesByImageResult result;

    public DisplayFaces(SearchFacesByImageResult facesResult, BufferedImage bufImage) throws Exception {
        super();
        scale = 1; // increase to shrink image size.

        result = facesResult;
        image = bufImage;


    }
    // Draws the bounding box around the detected faces.
    public void paintComponent(Graphics g) {
        float left = 0;
        float top = 0;
        int height = image.getHeight(this);
        int width = image.getWidth(this);

        Graphics2D g2d = (Graphics2D) g; // Create a Java2D version of g.

        // Draw the image.
        g2d.drawImage(image, 0, 0, width / scale, height / scale, this);
        g2d.setColor(new Color(0, 212, 0));

        // Iterate through faces and display bounding boxes.

        BoundingBox box = result.getSearchedFaceBoundingBox();
        left = width * box.getLeft();
        top = height * box.getTop();
        g2d.drawRect(Math.round(left / scale), Math.round(top / scale),
                Math.round((width * box.getWidth()) / scale), Math.round((height * box.getHeight())) / scale);
    }

}
