package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import pokeraidbot.domain.*;

import java.util.Locale;

public class RemoveSignUpCommand extends ConfigAwareCommand {
    private final GymRepository gymRepository;
    private final RaidRepository raidRepository;
    private final LocaleService localeService;

    public RemoveSignUpCommand(GymRepository gymRepository, RaidRepository raidRepository, LocaleService localeService,
                               ConfigRepository configRepository) {
        super(configRepository);
        this.gymRepository = gymRepository;
        this.raidRepository = raidRepository;
        this.localeService = localeService;
        this.name = "remove";
        this.help = localeService.getMessageFor(LocaleService.REMOVE_SIGNUP_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        try {
            final String user = commandEvent.getAuthor().getName();
            final Locale localeForUser = localeService.getLocaleForUser(user);
            String gymName = commandEvent.getArgs();
            final Gym gym = gymRepository.search(user, gymName, config.region);
            final Raid raid = raidRepository.getRaid(gym, config.region);
            final SignUp removed = raid.remove(user, raidRepository);
            if (removed != null) {
                commandEvent.reply(localeService.getMessageFor(LocaleService.SIGNUP_REMOVED, localeForUser,
                        gym.getName(), removed.toString()));
            } else {
                final String message =
                        localeService.getMessageFor(LocaleService.NO_SIGNUP_AT_GYM, localeForUser, user, gym.getName());
                commandEvent.reply(message);
            }
        } catch (RuntimeException e) {
            commandEvent.reply(e.getMessage());
        }
    }
}
