package pokeraidbot.domain.pokemon;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import net.dv8tion.jda.core.entities.User;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pokeraidbot.domain.config.LocaleService;
import pokeraidbot.infrastructure.JsonPokemon;
import pokeraidbot.infrastructure.JsonPokemons;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class PokemonRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(PokemonRepository.class);

    private final LocaleService localeService;
    private Map<String, Pokemon> pokemons = new LinkedHashMap<>();

    public PokemonRepository(String resourceName, LocaleService localeService) {
        this.localeService = localeService;
        try {
            final InputStream inputStream = PokemonRepository.class.getResourceAsStream(resourceName);
            ObjectMapper mapper = new ObjectMapper();
            JsonPokemons jsonPokemons = mapper.readValue(inputStream, JsonPokemons.class);
            for (JsonPokemon p : jsonPokemons.getPokemons()) {
                if (p != null && p.getName() != null && p.getTypes() != null) {
                    final Pokemon pokemon = new Pokemon(p.getNumber(), p.getName(), p.getAbout(),
                            new PokemonTypes(p.getTypes()), p.getBuddyDistance(),
                            new HashSet<>(Arrays.asList(p.getWeaknesses())),
                            new HashSet<>(Arrays.asList(p.getResistant())));
                    pokemons.put(p.getName().toUpperCase(), pokemon);
                }
            }
            LOGGER.info("Parsed " + jsonPokemons.getPokemons().size() + " pokemons.");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Pokemon search(String name, User user) {
        final Pokemon pokemon = fuzzySearch(name);
        if (pokemon == null) {
            final Locale locale = user == null ? LocaleService.DEFAULT : localeService.getLocaleForUser(user);
            throw new RuntimeException(localeService.getMessageFor(LocaleService.NO_POKEMON, locale, name));
        }
        return pokemon;
    }

    public Pokemon getByName(String name) {
        if (name == null) {
            return null;
        }
        final String nameToGet = name.trim().toUpperCase();
        final Pokemon pokemon = pokemons.get(nameToGet);
        return pokemon;
    }

    // This method is a getter with fuzzy search that doesn't throw an exception if it doesn't find a pokemon, just returns null
    public Pokemon fuzzySearch(String name) {
        final Collection<Pokemon> allPokemons = pokemons.values();

        final String nameToSearchFor = name.trim().toUpperCase();
        final Optional<Pokemon> pokemon = Optional.ofNullable(pokemons.get(nameToSearchFor));
        if (pokemon.isPresent()) {
            return pokemon.get();
        } else {
            List<ExtractedResult> candidates = FuzzySearch.extractTop(nameToSearchFor,
                    allPokemons.stream().map(p -> p.getName().toUpperCase()).collect(Collectors.toList()), 5, 50);
            if (candidates.size() == 1) {
                return pokemons.get(candidates.iterator().next().getString());
            } else if (candidates.size() < 1) {
                return null;
            } else {
                int score = 0;
                String highestScoreResultName = null;
                for (ExtractedResult result : candidates) {
                    if (result.getScore() > score) {
                        score = result.getScore();
                        highestScoreResultName = result.getString();
                    }
                }
                if (highestScoreResultName != null) {
                    return pokemons.get(highestScoreResultName);
                } else {
                    return null;
                }
            }
        }
    }

    public Set<Pokemon> getAll() {
        return Collections.unmodifiableSet(new HashSet<>(pokemons.values()));
    }

    public Pokemon getByNumber(Integer pokemonNumber) {
        for (Pokemon p : getAll()) {
            if (Objects.equals(p.getNumber(), pokemonNumber)) {
                return p;
            }
        }
        throw new RuntimeException(localeService.getMessageFor(LocaleService.NO_POKEMON, LocaleService.DEFAULT, "" +
                pokemonNumber));
    }
}
