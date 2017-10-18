package pokeraidbot.domain.tracking;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.StringUtils;
import pokeraidbot.commands.NewRaidCommand;
import pokeraidbot.commands.NewRaidExCommand;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.jpa.config.Config;

import java.util.Locale;

public class PokemonTrackingTarget implements TrackingTarget, Comparable<PokemonTrackingTarget> {
    private String region;
    private String userId;
    private String pokemonName;

    public PokemonTrackingTarget(String region, String userId, String pokemonName) {
        this.region = region;
        this.userId = userId;
        this.pokemonName = pokemonName;
    }

    public String getRegion() {
        return region;
    }

    public String getUserId() {
        return userId;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    @Override
    public boolean canHandle(CommandEvent commandEvent, Command command) {
        if (commandEvent.getAuthor().isBot()) {
            return false; // Skip bot messages
        }
        if (commandEvent.getAuthor().getId().equals(userId)) {
            return false; // Skip raids user created
        }
        if (command instanceof NewRaidCommand || command instanceof NewRaidExCommand) {
            boolean rawContentContainsPokemonName =
                    StringUtils.containsIgnoreCase(commandEvent.getEvent().getMessage().getRawContent(), pokemonName);
            return rawContentContainsPokemonName;
        }
        return false;
    }

    @Override
    public void handle(CommandEvent commandEvent, Command command, LocaleService localeService, Locale locale, Config config) {
        final Member memberById = commandEvent.getGuild().getMemberById(Long.parseLong(userId));
        final User user = memberById.getUser();
        final String userName = commandEvent.getEvent().getAuthor().getName();
        final String rawContent = commandEvent.getEvent().getMessage().getRawContent();

        final String message = localeService.getMessageFor(LocaleService.TRACKED_RAID, locale, pokemonName, userName, rawContent);
        sendPrivateMessage(user, message);
    }

    private void sendPrivateMessage(User user, String content)
    {
        // openPrivateChannel provides a RestAction<PrivateChannel>
        // which means it supplies you with the resulting channel
        user.openPrivateChannel().queue((channel) ->
        {
            // value is a parameter for the `accept(T channel)` method of our callback.
            // here we implement the body of that method, which will be called later by JDA automatically.
            channel.sendMessage(content).queue();
            // here we access the enclosing scope variable -content-
            // which was provided to sendPrivateMessage(User, String) as a parameter
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PokemonTrackingTarget)) return false;

        PokemonTrackingTarget that = (PokemonTrackingTarget) o;

        if (region != null ? !region.equals(that.region) : that.region != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return pokemonName != null ? pokemonName.equals(that.pokemonName) : that.pokemonName == null;
    }

    @Override
    public int hashCode() {
        int result = region != null ? region.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (pokemonName != null ? pokemonName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PokemonTrackingTarget{" +
                "region='" + region + '\'' +
                ", userId='" + userId + '\'' +
                ", pokemonName='" + pokemonName + '\'' +
                '}';
    }

    @Override
    public int compareTo(PokemonTrackingTarget o) {
        return toString().compareTo(o.toString());
    }
}
