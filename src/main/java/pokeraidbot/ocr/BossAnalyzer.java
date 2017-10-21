package pokeraidbot.ocr;

import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pokeraidbot.ocr.ImageTransforms.*;
import static pokeraidbot.ocr.OCRAnswer.OCRAnswerBuilder.anOCRAnswer;
import static pokeraidbot.ocr.RectangleRepo.getRectangle;
import static pokeraidbot.ocr.TextHelper.*;

public class BossAnalyzer {

    private static final Logger logger = Logger.getLogger(BossAnalyzer.class);

    public static OCRAnswer analyzeImage(String filePath) {
        File imageFile = new File(filePath);
        ITesseract instance = new Tesseract();
        instance.setLanguage("eng+swe");

        OCRAnswer.OCRAnswerBuilder builder = anOCRAnswer();
        builder.withType(OCRAnswer.TYPE.BOSS);
        builder.withSource(imageFile.getName());

        try {
            BufferedImage image = new MarvinImage(ImageIO.read(imageFile)).getBufferedImageNoAlpha();
            String ocr;

            instance.setPageSegMode(6);
            Pattern timePattern = Pattern.compile("((\\d|O)(\\d|O)(:|I|1)(\\d|O)(\\d|O))");
            ocr = instance.doOCR(blur(filterBlackAndWhite(image, 100,25 ), 1), getRectangle(RectangleType.BOSS_SYSTEM_TIME, image.getWidth(), image.getHeight()));
            Matcher timeMatcher = timePattern.matcher(cleanUpTime(ocr));
            if (!timeMatcher.find()) {
                ocr = instance.doOCR(image, getRectangle(RectangleType.EGG_SYSTEM_TIME, image.getWidth(), image.getHeight()));
                timeMatcher = timePattern.matcher(cleanUpTime(ocr));
            }
            if(timeMatcher.find(0)) {
                builder.withTime(postCleanupTime(timeMatcher.group(1)));
            }

            instance.setPageSegMode(6);
            ocr = instance.doOCR(dilate(filterBlackAndWhite(image, 50, 0), 2), getRectangle(RectangleType.BOSS_GYM, image.getWidth(), image.getHeight()));
            builder.withGymName(cleanUpText(ocr));

            instance.setPageSegMode(6);
            Pattern countdownPattern = Pattern.compile("(\\d|O):(\\d|O)(\\d|O):(\\d|O)(\\d|O)", Pattern.MULTILINE);
            ocr = instance.doOCR(filterBlackAndWhite(image, 25, 25), getRectangle(RectangleType.BOSS_COUNTDOWN, image.getWidth(), image.getHeight()));
            Matcher countdownMatcher = countdownPattern.matcher(cleanUpTime(ocr));
            if (countdownMatcher.find()) {
                builder.withCountdown(countdownMatcher.group(0));
            }

            ocr = instance.doOCR(filterBlackAndWhite(image, 2, 0), getRectangle(RectangleType.BOSS_NAME, image.getWidth(), image.getHeight()));
            builder.withPokemon(cleanUpPokemon(ocr));

            return builder.build();
        } catch (Exception e) {
            logger.warn("Unable to get OCR result from image" ,e);
            throw  new RuntimeException("Error reading OCR data");
        }
    }

    private static String cleanUpPokemon(String ocr) {
        return ocr.toUpperCase().replaceAll("[^A-Za-z0-9\\.\\-\\: ]", "");
    }

    private static void drawRectangles(File imageFile) throws IOException {
        MarvinImage marvinImage = new MarvinImage(ImageIO.read(imageFile));
        marvinImage = drawSegments(marvinImage, getRectangle(RectangleType.BOSS_SYSTEM_TIME, marvinImage.getWidth(), marvinImage.getHeight()));
        marvinImage = drawSegments(marvinImage, getRectangle(RectangleType.BOSS_COUNTDOWN, marvinImage.getWidth(), marvinImage.getHeight()));
        marvinImage = drawSegments(marvinImage, getRectangle(RectangleType.BOSS_GYM, marvinImage.getWidth(), marvinImage.getHeight()));
        marvinImage = drawSegments(marvinImage, getRectangle(RectangleType.BOSS_NAME, marvinImage.getWidth(), marvinImage.getHeight()));
        MarvinImageIO.saveImage(marvinImage, "img-out/" + imageFile.getName() + "-rectangle.png");
    }
}

