package pokeraidbot.infrastructure;

import pokeraidbot.domain.pokemon.Pokemon;
import pokeraidbot.domain.pokemon.PokemonRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class CounterTextFileParser {
    private Set<CounterPokemon> bestCounters = new LinkedHashSet<>();
    private Set<CounterPokemon> goodCounters = new LinkedHashSet<>();

    // Yes, the parsing code here is as fugly as it gets, but hey it works
    // The data files were just copy pasted text from a raid boss web site, because I'm lazy
    public CounterTextFileParser(String path, String pokemonName, PokemonRepository pokemonRepository) {
        try {
            final InputStream inputStream = CounterTextFileParser.class.getResourceAsStream(path + "/" + pokemonName.toLowerCase() + ".txt");
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = br.readLine();
            if (!line.contains("Supreme Counters")) {
                throw new IllegalStateException("Not properly formatted file!");
            }
            boolean supreme = true;
            boolean supremeDone = false;
            br.readLine();
            while (line != null) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                line = br.readLine();
                if (line == null) {
                    break;
                }
                String counterPokemonName = line.trim();
                // Ensure we can get the pokemon from the repository
                final Pokemon p = pokemonRepository.getByName(counterPokemonName);
                if (p == null) {
                    throw new IllegalStateException("Could not find pokemon in repository: " + counterPokemonName);
                }
                if (counterPokemonName != null && counterPokemonName.length() > 0) {
                    Set<String> moves = new HashSet<>();
                    while (((line = br.readLine()) != null) && !(line.equals(""))) {
                        final String trimmedLine = line.trim();
                        final Pokemon pokemon = pokemonRepository.getPokemon(trimmedLine);
                        if (pokemon != null) {
                            break;
                        }
                        if ((!trimmedLine.contains("Counters")) && (!trimmedLine.contains("Quick Move")) && (!trimmedLine.contains("Charge Move"))) {
                            moves.add(trimmedLine);
                        }
                        if (trimmedLine.contains("Good Counters")) {
                            line = br.readLine();
                            supremeDone = true;
                            break;
                        }
                    }
                    CounterPokemon counterPokemon = new CounterPokemon(counterPokemonName, moves);
                    if (supreme) {
                        bestCounters.add(counterPokemon);
                    } else {
                        goodCounters.add(counterPokemon);
                    }
                    if (supremeDone) {
                        supreme = false;
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Set<CounterPokemon> getBestCounters() {
        return bestCounters;
    }

    public Set<CounterPokemon> getGoodCounters() {
        return goodCounters;
    }
}
