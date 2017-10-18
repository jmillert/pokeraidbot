package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ConfigRepository;

import java.util.Locale;

public class UsageCommand extends ConfigAwareCommand {
    private final LocaleService localeService;

    public UsageCommand(LocaleService localeService, ConfigRepository configRepository, CommandListener commandListener) {
        super(configRepository, commandListener);
        this.localeService = localeService;
        this.name = "usage";
        this.help = localeService.getMessageFor(LocaleService.USAGE_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        final String args = commandEvent.getArgs();
        Locale locale;
        if (args != null && args.length() > 0) {
            locale = new Locale(args);
        } else {
            locale = localeService.getLocaleForUser(commandEvent.getAuthor().getName());
        }
        replyBasedOnConfig(config, commandEvent, localeService.getMessageFor(LocaleService.USAGE, locale));
    }
}
