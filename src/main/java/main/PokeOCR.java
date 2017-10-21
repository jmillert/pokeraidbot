package main;

import pokeraidbot.ocr.BossAnalyzer;
import pokeraidbot.ocr.EggAnalyzer;

import java.text.MessageFormat;

public class PokeOCR
{
    public static void main( String[] args ) {
        analyzeBosses();
        analyzeEggs();
    }

    private static void analyzeEggs() {
        String fileName = "img-ocr/eggs/image ({0}).png";
        for(int i =0 ; i <= 23; i++) {
            System.out.println(EggAnalyzer.analyzeImage(MessageFormat.format(fileName, i)));
        }
        System.out.println(EggAnalyzer.analyzeImage(MessageFormat.format(fileName, 22)));
    }

    private static void analyzeBosses() {
        String fileName = "img-ocr/bosses/image ({0}).png";
        for(int i =0 ; i <= 20; i++) {
            System.out.println(BossAnalyzer.analyzeImage(MessageFormat.format(fileName, i)));
        }
        System.out.println(BossAnalyzer.analyzeImage(MessageFormat.format(fileName, 15)));

    }
}
