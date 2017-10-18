package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.gym.Gym;
import pokeraidbot.domain.gym.GymRepository;
import pokeraidbot.domain.raid.Raid;
import pokeraidbot.domain.raid.RaidRepository;
import pokeraidbot.domain.raid.signup.SignUp;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.util.Locale;

public class RemoveSignUpCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;

    public RemoveSignUpCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService,
                               ConfigRepository configRepository, CommandListener commandListener) {
        super(configRepository, commandListener);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
        this.localeService = localeService;
        this.name = "remove";
        this.help = localeService.getMessageFor(LocaleService.REMOVE_SIGNUP_HELP, LocaleService.DEFAULT);
        this.aliases = new String[]{"unsign"};
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String user = commandEvent.getAuthor().getName();
        final Locale localeForUser = localeService.getLocaleForUser(user);
        String gymName = commandEvent.getArgs();
        final Gym gym = gymRepository.search(user, gymName, config.getRegion());
        final Raid raid = raidRepository.getActiveRaidOrFallbackToExRaid(gym, config.getRegion());
        final SignUp removed = raid.remove(user, raidRepository);
        if (removed != null) {
            commandEvent.reactSuccess();
        } else {
            final String message =
                    localeService.getMessageFor(LocaleService.NO_SIGNUP_AT_GYM, localeForUser, user, gym.getName());
            replyBasedOnConfigAndRemoveAfter(config, commandEvent, message, 15);
        }
    }
}
