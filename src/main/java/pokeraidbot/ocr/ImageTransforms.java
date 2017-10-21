package pokeraidbot.ocr;

import marvin.MarvinPluginCollection;
import marvin.color.MarvinColorModelConverter;
import marvin.image.MarvinImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.sqrt;

public class ImageTransforms {

    public static BufferedImage dilate(BufferedImage image, int matrixSize) throws IOException {
        boolean[][] matrix   = new boolean[matrixSize][matrixSize];
        for (int i=0 ; i<matrix.length; i++) {
            for (int j=0; j<matrix.length; j++) {
                matrix[i][j] = true;
            }
        }

        MarvinImage resultImage = MarvinColorModelConverter.rgbToBinary(new MarvinImage(image), 250);
        MarvinPluginCollection.morphologicalDilation(resultImage.clone(), resultImage, matrix);
        resultImage = MarvinColorModelConverter.binaryToRgb(resultImage);
        ImageIO.write(resultImage.getBufferedImageNoAlpha(), "png", new File("img-out/dilate.png"));

        return resultImage.getBufferedImageNoAlpha();
    }

    public static BufferedImage erode(BufferedImage image, int matrixSize) throws IOException {
        boolean[][] matrix   = new boolean[matrixSize][matrixSize];
        for (int i=0 ; i<matrix.length; i++) {
            for (int j=0; j<matrix.length; j++) {
                matrix[i][j] = true;
            }
        }

        MarvinImage resultImage = MarvinColorModelConverter.rgbToBinary(new MarvinImage(image), 250);
        MarvinPluginCollection.morphologicalErosion(resultImage.clone(), resultImage, matrix);
        resultImage = MarvinColorModelConverter.binaryToRgb(resultImage);
        ImageIO.write(resultImage.getBufferedImageNoAlpha(), "png", new File("img-out/erode.png"));

        return resultImage.getBufferedImageNoAlpha();
    }

    public static BufferedImage blur(BufferedImage image, int radius) throws IOException {

        MarvinImage filteredImage = new MarvinImage(image);
        MarvinPluginCollection.gaussianBlur(filteredImage.clone(), filteredImage, radius);

        ImageIO.write(filteredImage.getBufferedImageNoAlpha(), "png", new File("img-out/blur.png"));
        return filteredImage.getBufferedImageNoAlpha();
    }

    public static BufferedImage filterBlackAndWhite(BufferedImage img, double whiteTolerance, double blackTolerance) throws IOException {
        MarvinImage mi = new MarvinImage(img);
        MarvinPluginCollection.grayScale(mi);
        BufferedImage image = mi.getBufferedImageNoAlpha();
        ImageIO.write(image, "png", new File("img-out/filterBlackAndWhite-pre.png"));
        BufferedImage filteredImage = new BufferedImage(image.getWidth(), image.getHeight(), TYPE_INT_RGB);

        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                int argb = image.getRGB(x, y);

                Color color = new Color(argb);
                Color white = Color.decode("#FEFEFE");
                double whiteDistance = sqrt(Math.pow(white.getRed()-color.getRed(), 2)+ Math.pow(white.getGreen()-color.getGreen(), 2)+Math.pow(white.getBlue()-color.getBlue(), 2));
                Color black = Color.BLACK;
                double blackDistance = sqrt(Math.pow(black.getRed()-color.getRed(),2)+Math.pow(black.getGreen()-color.getGreen(),2)+Math.pow(black.getBlue()-color.getBlue(),2));

                if (whiteDistance<whiteTolerance || blackDistance<blackTolerance){
                    filteredImage.setRGB(x, y, 0x00000000);
                } else {
                    filteredImage.setRGB(x, y, 0x00FFFFFF);
                }
            }
        }
        return filteredImage;
    }

    public static MarvinImage drawSegments(MarvinImage image, Rectangle r){
        image.drawRect(r.x, r.y, r.width, r.height, Color.red);
        image.drawRect(r.x+1, r.y+1, r.width-2, r.height-2, Color.red);
        return image;
    }
}

