package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.BotService;
import pokeraidbot.Utils;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.SignUp;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

import java.util.Locale;
import java.util.Set;

import static pokeraidbot.Utils.getNamesOfThoseWithSignUps;
import static pokeraidbot.Utils.printTimeIfSameDay;

/**
 * !raid status [Pokestop name]
 */
public class RaidStatusCommand extends ConfigAwareCommand {
    private static final transient Logger LOGGER = LoggerFactory.getLogger(RaidStatusCommand.class);
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;
    private final BotService botService;
    private final PokemonRepository pokemonRepository;

    public RaidStatusCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService,
                             ServerConfigRepository serverConfigRepository, BotService botService, CommandListener commandListener,
                             PokemonRepository pokemonRepository) {
        super(serverConfigRepository, commandListener, localeService);
        this.localeService = localeService;
        this.botService = botService;
        this.pokemonRepository = pokemonRepository;
        this.name = "status";
        this.help = localeService.getMessageFor(LocaleService.RAIDSTATUS_HELP, LocaleService.DEFAULT);

        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String gymName = commandEvent.getArgs();
        final User user = commandEvent.getAuthor();
        final String userName = user.getName();
        final Gym gym = gymRepository.search(user, gymName, config.getRegion());
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion(), user);
        final Set<SignUp> signUps = raid.getSignUps();
        final int numberOfPeople = raid.getNumberOfPeopleSignedUp();

        final Locale localeForUser = localeService.getLocaleForUser(user);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(null, null, null);
        final Pokemon pokemon = raid.getPokemon();
        embedBuilder.setTitle(localeService.getMessageFor(LocaleService.RAIDSTATUS, localeForUser, gym.getName()),
                Utils.getNonStaticMapUrl(gym));
        StringBuilder sb = new StringBuilder();
        final String activeText = localeService.getMessageFor(LocaleService.ACTIVE, localeForUser);
        final String startGroupText = localeService.getMessageFor(LocaleService.START_GROUP, localeForUser);
        final String findYourWayText = localeService.getMessageFor(LocaleService.FIND_YOUR_WAY, localeForUser);
        final String raidBossText = localeService.getMessageFor(LocaleService.RAID_BOSS, localeForUser);
//        final String hintsText = localeService.getMessageFor(LocaleService.FOR_HINTS, localeForUser);
        final Set<String> signUpNames = getNamesOfThoseWithSignUps(raid.getSignUps(), true);
        final String allSignUpNames = StringUtils.join(signUpNames, ", ");

        sb.append("**").append(activeText).append(":** ")
                .append(printTimeIfSameDay(raid.getEndOfRaid().minusHours(1)))
                .append("-").append(printTimeIfSameDay(raid.getEndOfRaid()))
                .append("\t**").append(numberOfPeople).append(" ")
                .append(localeService.getMessageFor(LocaleService.SIGNED_UP, localeForUser)).append("**")
                .append(signUps.size() > 0 ? ":\n" + allSignUpNames : "").append("\n").append(startGroupText)
                .append(":\n*!raid group ")
                .append(printTimeIfSameDay(raid.getEndOfRaid().minusMinutes(15))).append(" ")
                .append(gymName).append("*\n")
                .append(raidBossText).append(" **").append(pokemon).append("** - ") //.append(hintsText)
                .append("*!raid vs ").append(pokemon.getName()).append("*");
        embedBuilder.setFooter(findYourWayText + localeService.getMessageFor(LocaleService.GOOGLE_MAPS,
                localeService.getLocaleForUser(user)),
                Utils.getPokemonIcon(pokemon));
        embedBuilder.setDescription(sb.toString());
        final MessageEmbed messageEmbed = embedBuilder.build();

        commandEvent.reply(messageEmbed);
    }
}
