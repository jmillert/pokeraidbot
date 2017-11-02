package pokeraidbot;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.errors.UserMessedUpException;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.signup.SignUp;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Utils {
    public static final DateTimeFormatter timeParseFormatter = DateTimeFormatter.ofPattern("H[:][.]mm");
    public static final DateTimeFormatter dateAndTimeParseFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH[:][.]mm");
    public static final DateTimeFormatter timePrintFormatter = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter dateAndTimePrintFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final int HIGH_LIMIT_FOR_SIGNUPS = 20;
    private static ClockService clockService = new ClockService();

    public static ClockService getClockService() {
        return clockService;
    }

    public static void setClockService(ClockService clockService) {
        Utils.clockService = clockService;
    }

    public static String printDateTime(LocalDateTime dateTime) {
        return dateTime.format(dateAndTimePrintFormatter);
    }

    public static String printTime(LocalTime time) {
        return time.format(timePrintFormatter);
    }

    public static String printTimeIfSameDay(LocalDateTime dateAndTime) {
        if (dateAndTime.toLocalDate().isEqual(LocalDate.now())) {
            return dateAndTime.toLocalTime().format(timePrintFormatter);
        } else {
            return printDateTime(dateAndTime);
        }
    }

    public static boolean isTypeDoubleStrongVsPokemonWithTwoTypes(String pokemonTypeOne,
                                                                  String pokemonTypeTwo, String typeToCheck) {
        Validate.notEmpty(pokemonTypeOne);
        Validate.notEmpty(pokemonTypeTwo);
        Validate.notEmpty(typeToCheck);
        if (typeIsStrongVsPokemon(pokemonTypeOne, typeToCheck) && typeIsStrongVsPokemon(pokemonTypeTwo, typeToCheck)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean typeIsStrongVsPokemon(String pokemonType, String typeToCheck) {
        Validate.notEmpty(pokemonType);
        Validate.notEmpty(typeToCheck);
        String check = typeToCheck.toUpperCase().trim();
        switch (pokemonType.toUpperCase().trim()) {
            case "NORMAL":
                return check.equals("FIGHTING");
            case "FIRE":
                return Arrays.asList("WATER", "GROUND", "ROCK").contains(check);
            case "WATER":
                return Arrays.asList("ELECTRIC", "GRASS").contains(check);
            case "ELECTRIC":
                return Arrays.asList("GROUND").contains(check);
            case "GRASS":
                return Arrays.asList("FIRE", "ICE", "POISON", "FLYING", "BUG").contains(check);
            case "ICE":
                return Arrays.asList("FIRE", "FIGHTING", "ROCK", "STEEL").contains(check);
            case "FIGHTING":
                return Arrays.asList("FLYING", "PSYCHIC", "FAIRY").contains(check);
            case "POISON":
                return Arrays.asList("PSYCHIC", "GROUND").contains(check);
            case "GROUND":
                return Arrays.asList("WATER", "GRASS", "ICE").contains(check);
            case "FLYING":
                return Arrays.asList("ELECTRIC", "ICE", "ROCK").contains(check);
            case "PSYCHIC":
                return Arrays.asList("BUG", "GHOST", "DARK").contains(check);
            case "BUG":
                return Arrays.asList("FIRE", "FLYING", "ROCK").contains(check);
            case "ROCK":
                return Arrays.asList("WATER", "GRASS", "FIGHTING", "GROUND", "STEEL").contains(check);
            case "GHOST":
                return Arrays.asList("GHOST", "DARK").contains(check);
            case "DRAGON":
                return Arrays.asList("ICE", "DRAGON", "FAIRY").contains(check);
            case "DARK":
                return Arrays.asList("BUG", "FIGHTING", "FAIRY").contains(check);
            case "STEEL":
                return Arrays.asList("FIRE", "FIGHTING", "GROUND").contains(check);
            case "FAIRY":
                return Arrays.asList("POISON", "STEEL").contains(check);
        }
        return false;
    }

    public static void assertSignupTimeNotBeforeNow(User user, LocalDateTime dateAndTime, LocaleService localeService) {
        final LocalDateTime now = clockService.getCurrentDateTime();
        if (dateAndTime.isBefore(now)) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.SIGN_BEFORE_RAID, localeService.getLocaleForUser(user),
                            printTimeIfSameDay(dateAndTime), printTimeIfSameDay(now)));
        }
    }

    public static void assertCreateRaidTimeNotBeforeNow(User user, LocalDateTime dateAndTime,
                                                        LocaleService localeService) {
        final LocalDateTime now = clockService.getCurrentDateTime();
        if (dateAndTime.isBefore(now)) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.TIMEZONE, localeService.getLocaleForUser(user),
                            printTimeIfSameDay(dateAndTime), printTimeIfSameDay(now)));
        }
    }

    public static void assertTimeNotInNoRaidTimespan(User user, LocalTime time, LocaleService localeService) {
        if (time.isAfter(LocalTime.of(22, 00)) || time.isBefore(LocalTime.of(6, 0))) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.NO_RAIDS_NOW, localeService.getLocaleForUser(user),
                            printTime(time)));
        }
    }

    public static void assertTimeNotMoreThanXHoursFromNow(User user, LocalTime time,
                                                          LocaleService localeService, Integer hours) {
        final LocalTime now = clockService.getCurrentTime();
        if (now.plusHours(2).isBefore(time)) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.NO_RAID_TOO_LONG, localeService.getLocaleForUser(user),
                            printTime(time), printTime(now), String.valueOf(hours)));
        }
    }

    public static void assertEtaNotAfterRaidEnd(User user, Raid raid, LocalDateTime eta, LocaleService localeService) {
        if (eta.isAfter(raid.getEndOfRaid())) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.NO_ETA_AFTER_RAID,
                            localeService.getLocaleForUser(user), printTimeIfSameDay(eta),
                            printTimeIfSameDay(raid.getEndOfRaid())));
        }
    }

    public static String getStaticMapUrl(Gym gym) {
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + gym.getX() + "," + gym.getY() +
                "&zoom=14&size=400x400&maptype=roadmap&markers=icon:http://millert.se/pogo/marker_xsmall.png%7C" +
                gym.getX() + "," + gym.getY() + "&key=AIzaSyAZm7JLojr2KaUvkeHEpHh0Y-zPwP3dpCU";
        return url;
    }

    public static String getNonStaticMapUrl(Gym gym) {
        String url = "http://maps.google.com/maps?q=loc:" + gym.getX() + "," + gym.getY();
        return url;
    }

    public static String printWeaknesses(Pokemon pokemon) {
        Set<String> weaknessesToPrint = new LinkedHashSet<>();
        final Set<String> typeSet = pokemon.getTypes().getTypeSet();
        for (String weakness : pokemon.getWeaknesses()) {
            if (typeSet.size() > 1) {
                final Iterator<String> iterator = typeSet.iterator();
                if (isTypeDoubleStrongVsPokemonWithTwoTypes(iterator.next(), iterator.next(), weakness)) {
                    weaknessesToPrint.add("**" + weakness + "**");
                } else {
                    weaknessesToPrint.add(weakness);
                }
            } else {
                weaknessesToPrint.add(weakness);
            }
        }

        return StringUtils.join(weaknessesToPrint, ", ");
    }

    public static boolean isSamePokemon(String pokemonName, String existingEntityPokemon) {
        return pokemonName.equalsIgnoreCase(existingEntityPokemon);
    }

    public static boolean oneIsMewTwo(String pokemonName, String existingEntityPokemon) {
        return pokemonName.equalsIgnoreCase("mewtwo") ||
                existingEntityPokemon.equalsIgnoreCase("mewtwo");
    }

    public static boolean raidsCollide(LocalDateTime endOfRaid, LocalDateTime endOfRaidTwo) {
        LocalDateTime startTime = endOfRaid.minusHours(1);
        LocalDateTime startTimeTwo = endOfRaidTwo.minusHours(1);
        return isInInterval(startTime, endOfRaid, startTimeTwo, endOfRaidTwo) ||
                isInInterval(startTimeTwo, endOfRaidTwo, startTime, endOfRaid);
    }

    private static boolean isInInterval(LocalDateTime startTime, LocalDateTime endOfRaid,
                                        LocalDateTime startTimeTwo, LocalDateTime endOfRaidTwo) {
        return (startTime.isAfter(startTimeTwo) && startTime.isBefore(endOfRaidTwo)) ||
                (endOfRaid.isBefore(endOfRaidTwo) && endOfRaid.isAfter(startTimeTwo));
    }

    public static boolean isRaidExPokemon(String pokemonName) {
        return pokemonName.equalsIgnoreCase("mewtwo");
    }

    public static LocalTime parseTime(User user, String timeString, LocaleService localeService) {
        LocalTime endsAtTime;
        try {
            timeString = preProcessTimeString(timeString);
            endsAtTime = LocalTime.parse(timeString, Utils.timeParseFormatter);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.BAD_DATETIME_FORMAT, localeService.getLocaleForUser(user),
                            "HH:MM", timeString));
        }
        return endsAtTime;
    }

    public static Integer assertNotTooManyOrNoNumber(User user, LocaleService localeService, String people) {
        Integer numberOfPeople;
        try {
            numberOfPeople = new Integer(people);
            if (numberOfPeople < 1 || numberOfPeople > HIGH_LIMIT_FOR_SIGNUPS) {
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.ERROR_PARSE_PLAYERS,
                            localeService.getLocaleForUser(user),
                            people, String.valueOf(HIGH_LIMIT_FOR_SIGNUPS)));
        }
        return numberOfPeople;
    }

    public static LocalDate parseDate(User user, String dateString, LocaleService localeService) {
        LocalDate theDate;
        try {
            theDate = LocalDate.parse(dateString);
        } catch (DateTimeException | NullPointerException e) {
            throw new UserMessedUpException(user,
                    localeService.getMessageFor(LocaleService.BAD_DATETIME_FORMAT,
                            localeService.getLocaleForUser(user), "yyyy-MM-dd", dateString));
        }
        return theDate;
    }

    public static Set<String> getNamesOfThoseWithSignUps(Set<SignUp> signUpsAt, boolean includeEta) {
        final Set<String> signUpNames;
        signUpNames = new LinkedHashSet<>();
        for (SignUp signUp : signUpsAt) {
            if (signUp.getHowManyPeople() > 0) {
                String text = signUp.getUserName() + " (**" + signUp.getHowManyPeople();
                if (includeEta) {
                   text = text + ", ETA " + printTime(signUp.getArrivalTime());
                }
                text = text + "**)";
                signUpNames.add(text);
            }
        }
        return signUpNames;
    }

    public static String getPokemonIcon(Pokemon pokemon) {
        return "https://raw.githubusercontent.com/kvangent/PokeAlarm/master/icons/" +
                pokemon.getNumber() + ".png";
    }

    public static String[] prepareArguments(CommandEvent commandEvent) {
        return commandEvent.getArgs().replaceAll("\\s{2,4}", " ").split(" ");
    }

    public static boolean isRaidEx(Raid raid) {
        return isRaidExPokemon(raid.getPokemon().getName());
    }

    private static String preProcessTimeString(String timeString) {
        if (timeString != null && timeString.matches("[0-9]{3,4}")) {
            return new StringBuilder(timeString).insert(timeString.length()-2, ":").toString();
        } else {
            return timeString;
        }
    }
}
