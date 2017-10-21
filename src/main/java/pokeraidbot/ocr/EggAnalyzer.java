package pokeraidbot.ocr;

import marvin.image.MarvinImage;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pokeraidbot.ocr.ImageTransforms.*;
import static pokeraidbot.ocr.OCRAnswer.OCRAnswerBuilder.anOCRAnswer;
import static pokeraidbot.ocr.RectangleRepo.getRectangle;
import static pokeraidbot.ocr.TextHelper.cleanUpText;
import static pokeraidbot.ocr.TextHelper.cleanUpTime;

public class EggAnalyzer {

    private static final Logger logger = Logger.getLogger(EggAnalyzer.class);

    public static OCRAnswer analyzeImage(String filePath) {
        File imageFile = new File(filePath);
        ITesseract instance = new Tesseract();
        instance.setLanguage("eng+swe");

        OCRAnswer.OCRAnswerBuilder builder = anOCRAnswer();
        builder.withType(OCRAnswer.TYPE.EGG);
        builder.withSource(imageFile.getName());

        try {
            BufferedImage image = new MarvinImage(ImageIO.read(imageFile)).getBufferedImageNoAlpha();
            String ocr;

            instance.setPageSegMode(7);
            Pattern timePattern = Pattern.compile("((\\d|O)(\\d|O)(:|I)(\\d|O)(\\d|O))");
            ocr = instance.doOCR(blur(filterBlackAndWhite(image, 100,25 ), 1),
                    getRectangle(RectangleType.EGG_SYSTEM_TIME, image.getWidth(), image.getHeight()));
            Matcher timeMatcher = timePattern.matcher(cleanUpTime(ocr));
            if (!timeMatcher.find()) {
                ocr = instance.doOCR(image, getRectangle(RectangleType.EGG_SYSTEM_TIME, image.getWidth(), image.getHeight()));
                timeMatcher = timePattern.matcher(cleanUpTime(ocr));
            }
            if(timeMatcher.find(0)) {
                builder.withTime(timeMatcher.group(1));
            }

            instance.setPageSegMode(6);
            ocr = instance.doOCR(dilate(filterBlackAndWhite(image, 50, 0), 2),
                    getRectangle(RectangleType.EGG_GYM, image.getWidth(), image.getHeight()));
            builder.withGymName(cleanUpText(ocr));

            instance.setPageSegMode(6);
            Pattern countdownPattern = Pattern.compile("(\\d|O):(\\d|O)(\\d|O):(\\d|O)(\\d|O)", Pattern.MULTILINE);
            ocr = instance.doOCR(filterBlackAndWhite(image, 25, 25),
                    getRectangle(RectangleType.EGG_COUNTDOWN, image.getWidth(), image.getHeight()));
            Matcher countdownMatcher = countdownPattern.matcher(cleanUpTime(ocr));
            if (countdownMatcher.find()) {
                builder.withCountdown(countdownMatcher.group(0));
            }

            return builder.build();
        } catch (Exception e) {
            logger.warn("Unable to get OCR result from image" ,e);
            throw  new RuntimeException("Error reading OCR data");
        }
    }
}

