package pokeraidbot.ocr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TextHelper {
    private static Set<String> KNOWN_POKEMON =
            new HashSet<>(Arrays.asList("SUICUNE", "TYRANITAR", "EXEGGUTOR",
                    "GENGAR", "BLASTOISE", "ALAKAZAM", "VENUSAUR",
                    "CHARIZARD", "SNORLAX", "JOLTEON", "RHYDON"));


    public static String matchPokemon(String s) {
        Optional<String> firstMatch = KNOWN_POKEMON.stream().filter(p -> s.toUpperCase().contains(p)).findFirst();
        return firstMatch.orElse("");
    }


    public static CharSequence cleanUpTime(String str) {
        return str.replaceAll("O", "0"). replaceAll("@", "0").replaceAll("o", "0")
                .replaceAll("l", "1");
    }

    public static String postCleanupTime(String str) {
        return str.substring(0,2) + ":" +str.substring(3,5);
    }
    public static String cleanUpText(String str) {
        str = str.replaceAll("‘","")
                .replaceAll("'", "").replaceAll("\n", " ").trim();

        // replace 0 with o if it has adjacent letters
        StringBuilder sb = new StringBuilder(str);
        for (int idx = 0; (idx = sb.indexOf("0", idx)) >= 0; idx++) {
            if(hasAdjacentLetter(sb, idx)) {
                sb.setCharAt(idx, 'o');
            }
        }
        // if | is followed by capital letter, it's probably an I otherwise an l
        for (int idx = 0; (idx = sb.indexOf("|", idx)) >= 0; idx++) {
            if(idx < sb.length()-1 && Character.toString(sb.charAt(idx+1)).matches("[A-Z]")) {
                sb.setCharAt(idx, 'I');
            } else {
                sb.setCharAt(idx, 'l');
            }
        }

        return sb.toString();
    }

    private static boolean hasAdjacentLetter(StringBuilder sb, int idx) {
        boolean letterBefore = idx>0 && sb.length()>0 &&
                Character.toString(sb.charAt(idx-1)).matches("\\p{IsLatin}");
        boolean letterAfter = idx<sb.length()-1 &&
                Character.toString(sb.charAt(idx+1)).matches("\\p{IsLatin}");
        return letterBefore || letterAfter;
    }
}
