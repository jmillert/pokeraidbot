package pokeraidbot.ocr;

import org.junit.Test;

import java.text.MessageFormat;

public class EggAnalyzerTest {
    @Test
    public void crackImage() throws Exception {
        String fileName = "img-ocr/eggs/image ({0}).png";
        OCRAnswer ocrAnswer = EggAnalyzer.analyzeImage(MessageFormat.format(fileName, 1));
    }

}