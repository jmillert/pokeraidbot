package pokeraidbot.domain;

import pokeraidbot.domain.errors.UserMessedUpException;

import java.time.LocalTime;
import java.util.*;

import static pokeraidbot.Utils.printTime;

public class Raid {
    private final Pokemon pokemon;
    private final LocalTime endOfRaid;
    private final Gym gym;
    private final LocaleService localeService;
    private Map<String, SignUp> signUps = new HashMap<>();
    private String region;

    public Raid(Pokemon pokemon, LocalTime endOfRaid, Gym gym, LocaleService localeService, String region) {
        this.pokemon = pokemon;
        this.endOfRaid = endOfRaid;
        this.gym = gym;
        this.localeService = localeService;
        this.region = region;
    }

    public Pokemon getPokemon() {
        return pokemon;
    }

    public LocalTime getEndOfRaid() {
        return endOfRaid;
    }

    public Gym getGym() {
        return gym;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Raid)) return false;

        Raid raid = (Raid) o;

        if (pokemon != null ? !pokemon.equals(raid.pokemon) : raid.pokemon != null) return false;
        if (endOfRaid != null ? !endOfRaid.equals(raid.endOfRaid) : raid.endOfRaid != null) return false;
        if (gym != null ? !gym.equals(raid.gym) : raid.gym != null) return false;
        if (signUps != null ? !signUps.equals(raid.signUps) : raid.signUps != null) return false;
        return region != null ? region.equals(raid.region) : raid.region == null;
    }

    @Override
    public int hashCode() {
        int result = pokemon != null ? pokemon.hashCode() : 0;
        result = 31 * result + (endOfRaid != null ? endOfRaid.hashCode() : 0);
        result = 31 * result + (gym != null ? gym.hashCode() : 0);
        result = 31 * result + (signUps != null ? signUps.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return localeService.getMessageFor(LocaleService.RAID_TOSTRING, LocaleService.DEFAULT, pokemon.toString(),
                gym.toString(), printTime(endOfRaid));
    }

    public void signUp(String userName, int howManyPeople, LocalTime arrivalTime, RaidRepository repository) {
        final SignUp signUp = signUps.get(userName);
        if (signUp != null) {
            throw new UserMessedUpException(userName, localeService.getMessageFor(LocaleService.ALREADY_SIGNED_UP,
                    LocaleService.DEFAULT, this.toString(), signUp.toString()));
        }
        final SignUp theSignUp = new SignUp(userName, howManyPeople, arrivalTime);
        signUps.put(userName, theSignUp);
        repository.addSignUp(userName, this, theSignUp);
    }

    public Set<SignUp> getSignUps() {
        return Collections.unmodifiableSet(new HashSet<>(signUps.values()));
    }

    public int getNumberOfPeopleSignedUp() {
        return signUps.values().stream().mapToInt(signup -> signup.getHowManyPeople()).sum();
    }

    public SignUp remove(String userName, RaidRepository raidRepository) {
        final SignUp removed = signUps.remove(userName);
        raidRepository.removeSignUp(userName, this, removed);
        return removed;
    }

    public void setSignUps(Map<String, SignUp> signUps) {
        this.signUps = signUps;
    }

    public String getRegion() {
        return region;
    }
}
