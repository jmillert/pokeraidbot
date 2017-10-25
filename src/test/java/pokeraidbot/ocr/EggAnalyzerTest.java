package pokeraidbot.ocr;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class EggAnalyzerTest {

    HashMap<Integer, OCRAnswer> gyms = new HashMap<>();
    @Before
    public void setUp() throws Exception {
        gyms.put(0, OCRAnswer.OCRAnswerBuilder.anOCRAnswer().withType(OCRAnswer.Type.EGG)
                .withSource("image (0).png").withTime("19:25").withCountdown("0:15:48")
                .withGymName("What Does The Fox Say?").build());
        gyms.put(5, OCRAnswer.OCRAnswerBuilder.anOCRAnswer().withType(OCRAnswer.Type.EGG)
                .withSource("image (5).png").withTime("12:48").withCountdown("0:18:43")
                .withGymName("Skeppet").build());
        gyms.put(10, OCRAnswer.OCRAnswerBuilder.anOCRAnswer().withType(OCRAnswer.Type.EGG)
                .withSource("image (10).png").withTime("18:29").withCountdown("0:45:19")
                .withGymName("Elisabethsjukhuset").build());

    }

    @Ignore
    @Test
    public void crackImage() throws Exception {
        String fileName = "img-ocr/eggs/image ({0}).png";
        assertEquals(EggAnalyzer.analyzeImage(MessageFormat.format(fileName, 0)),gyms.get(0));
        assertEquals(EggAnalyzer.analyzeImage(MessageFormat.format(fileName, 5)),gyms.get(5));
        assertEquals(EggAnalyzer.analyzeImage(MessageFormat.format(fileName, 10)),gyms.get(10));
    }

}