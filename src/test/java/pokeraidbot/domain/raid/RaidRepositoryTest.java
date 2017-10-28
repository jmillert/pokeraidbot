package pokeraidbot.domain.raid;

import net.dv8tion.jda.core.entities.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pokeraidbot.TestServerMain;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.ClockService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;
import pokeraidbot.infrastructure.jpa.raid.RaidEntityRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestServerMain.class})
public class RaidRepositoryTest {
    private static final String uppsalaRegion = "uppsala";
    @Autowired
    RaidRepository repo;
    @Autowired
    GymRepository gymRepository;
    @Autowired
    PokemonRepository pokemonRepository;
    @Autowired
    ClockService clockService;
    @Autowired
    LocaleService localeService;
    @Autowired
    ConfigRepository configRepository;
    @Autowired
    RaidEntityRepository raidEntityRepository;

    @Before
    public void setUp() throws Exception {
        Utils.setClockService(clockService);
        gymRepository = TestServerMain.getGymRepositoryForConfig(localeService, configRepository);
        pokemonRepository = new PokemonRepository("/mons.json", localeService);
        raidEntityRepository.deleteAllInBatch();
    }

    @Test
    public void testSignUp() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0)); // We're not allowed to create signups at night, so mocking time
        final LocalDateTime now = clockService.getCurrentDateTime();
        final LocalTime nowTime = now.toLocalTime();
        LocalDateTime endOfRaid = now.plusMinutes(45);
        final Gym gym = gymRepository.findByName("Blenda", uppsalaRegion);
        Raid enteiRaid = new Raid(pokemonRepository.getByName("Entei"), endOfRaid, gym, new LocaleService(), uppsalaRegion);
        String raidCreatorName = "testUser1";
        try {
            repo.newRaid(raidCreatorName, enteiRaid);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            fail("Could not save raid: " + e.getMessage());
        }
        Raid raid = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion);
        enteiRaid.setId(raid.getId()); // Set to same id for equals comparison
        enteiRaid.setCreator(raid.getCreator()); // Set creator to same for equals comparison
        assertThat(raid, is(enteiRaid));
        String userName = "testUser2";
        User user = mock(User.class);
        when(user.getName()).thenReturn(userName);
        int howManyPeople = 3;
        LocalTime arrivalTime = nowTime.plusMinutes(30);
        raid.signUp(user, howManyPeople, arrivalTime, repo);
        assertThat(raid.getSignUps().size(), is(1));
        assertThat(raid.getNumberOfPeopleSignedUp(), is(howManyPeople));

        final Raid raidFromDb = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion);
        assertThat(raidFromDb, is(raid));
        assertThat(raidFromDb.getSignUps().size(), is(1));
    }

    @Test
    public void changePokemonWorks() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0)); // We're not allowed to create signups at night, so mocking time
        final LocalDateTime now = clockService.getCurrentDateTime();
        final LocalTime nowTime = now.toLocalTime();
        LocalDateTime endOfRaid = now.plusMinutes(45);
        final Gym gym = gymRepository.findByName("Blenda", uppsalaRegion);
        Raid enteiRaid = new Raid(pokemonRepository.getByName("Entei"), endOfRaid, gym, new LocaleService(), uppsalaRegion);
        String raidCreatorName = "testUser1";
        try {
            repo.newRaid(raidCreatorName, enteiRaid);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            fail("Could not save raid: " + e.getMessage());
        }
        Raid raid = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion);
        Raid changedRaid = repo.changePokemon(raid, pokemonRepository.getByName("Mewtwo"));
        assertThat(raid.getEndOfRaid(), is(changedRaid.getEndOfRaid()));
        assertThat(raid.getGym(), is(changedRaid.getGym()));
        assertThat(raid.getSignUps(), is(changedRaid.getSignUps()));
        assertThat(raid.getRegion(), is(changedRaid.getRegion()));
        assertThat(raid.getPokemon().getName(), is("Entei"));
        assertThat(changedRaid.getPokemon().getName(), is("Mewtwo"));
    }

    @Test
    public void changeEndOfRaidWorks() throws Exception {
        clockService.setMockTime(LocalTime.of(10, 0)); // We're not allowed to create signups at night, so mocking time
        final LocalDateTime now = clockService.getCurrentDateTime();
        final LocalTime nowTime = now.toLocalTime();
        LocalDateTime endOfRaid = now.plusMinutes(45);
        final Gym gym = gymRepository.findByName("Blenda", uppsalaRegion);
        Raid enteiRaid = new Raid(pokemonRepository.getByName("Entei"), endOfRaid, gym, new LocaleService(), uppsalaRegion);
        String raidCreatorName = "testUser1";
        try {
            repo.newRaid(raidCreatorName, enteiRaid);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            fail("Could not save raid: " + e.getMessage());
        }
        Raid raid = repo.getActiveRaidOrFallbackToExRaid(gym, uppsalaRegion);
        Raid changedRaid = repo.changeEndOfRaid(raid, endOfRaid.plusMinutes(5));
        assertThat(raid.getEndOfRaid(), not(changedRaid.getEndOfRaid()));
        assertThat(changedRaid.getEndOfRaid(), is(raid.getEndOfRaid().plusMinutes(5)));
        assertThat(raid.getGym(), is(changedRaid.getGym()));
        assertThat(raid.getSignUps(), is(changedRaid.getSignUps()));
        assertThat(raid.getRegion(), is(changedRaid.getRegion()));
        assertThat(raid.getPokemon().getName(), is(changedRaid.getPokemon().getName()));
    }

    // todo: testcases for the intricate rules around EX raids
}
