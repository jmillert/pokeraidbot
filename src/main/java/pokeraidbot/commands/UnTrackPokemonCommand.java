package pokeraidbot.commands;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.CommandListener;
import net.dv8tion.jda.core.entities.User;
import pokeraidbot.BotService;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;
import pokeraidbot.domain.tracking.PokemonTrackingTarget;
import pokeraidbot.domain.tracking.TrackingCommandListener;
import pokeraidbot.infrastructure.jpa.config.Config;
import pokeraidbot.infrastructure.jpa.config.ServerConfigRepository;

public class UnTrackPokemonCommand extends ConfigAwareCommand {
    private final PokemonRepository pokemonRepository;
    private final TrackingCommandListener commandListener;

    public UnTrackPokemonCommand(BotService botService, ServerConfigRepository serverConfigRepository,
                                 LocaleService localeService,
                                 PokemonRepository pokemonRepository, CommandListener commandListener) {
        super(serverConfigRepository, commandListener, localeService);
        this.commandListener = botService.getTrackingCommandListener();
        this.pokemonRepository = pokemonRepository;
        this.name = "untrack";
        this.help = localeService.getMessageFor(LocaleService.UNTRACK_HELP, LocaleService.DEFAULT);
    }

    @Override
    protected void executeWithConfig(CommandEvent commandEvent, Config config) {
        String args = commandEvent.getArgs();
        final String userId = commandEvent.getAuthor().getId();
        final User user = commandEvent.getAuthor();
        if (args == null || args.length() < 1) {
            commandListener.removeAll(user);
            commandEvent.reactSuccess();
        } else {
            Pokemon pokemon = pokemonRepository.search(args, user);
            commandListener.remove(new PokemonTrackingTarget(config.getRegion(), userId, pokemon), user);
            commandEvent.reactSuccess();
        }
    }
}