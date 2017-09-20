package main;

import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pokeraidbot.BotService;
import pokeraidbot.domain.*;
import pokeraidbot.infrastructure.CSVGymDataReader;
import pokeraidbot.infrastructure.jpa.RaidEntityRepository;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = "pokeraidbot.infrastructure.jpa")
@EnableJpaRepositories(basePackages = "pokeraidbot.infrastructure.jpa")
@ComponentScan(basePackages = {"pokeraidbot"})
@EnableTransactionManagement
public class BotServerMain {
    public static void main(String[] args) throws InterruptedException, IOException, LoginException, RateLimitedException {
        SpringApplication.run(BotServerMain.class, args);
    }

    @Bean
    public LocaleService getLocaleService() {
        return new LocaleService();
    }

    @Bean
    public ClockService getClockService() {
        return new ClockService();
    }

    @Bean
    public BotService getBotService(LocaleService localeService, GymRepository gymRepository, RaidRepository raidRepository,
                                    PokemonRepository pokemonRepository, PokemonRaidStrategyService raidInfoService,
                                    ConfigRepository configRepository) {
        return new BotService(localeService, gymRepository, raidRepository, pokemonRepository, raidInfoService,
                configRepository);
    }

    @Bean
    public GymRepository getGymRepository(LocaleService localeService, ConfigRepository configRepository) {
        Map<String, Config> configMap = configRepository.getAllConfig();
        Map<String, Set<Gym>> gymsPerRegion = new HashMap<>();
        System.out.println("Config has following servers: " + configMap.keySet());
        for (String server : configMap.keySet()) {
            final Config config = configRepository.getConfigForServer(server);
            final Set<Gym> existingGyms = gymsPerRegion.get(config.region);
            if (existingGyms == null) {
                final Set<Gym> gymsInRegion = new CSVGymDataReader("/gyms_" + config.region + ".csv").readAll();
                gymsPerRegion.put(config.region, gymsInRegion);
                System.out.println("Loaded " + gymsInRegion.size() + " gyms for region " + config.region + ".");
            }
        }
        return new GymRepository(gymsPerRegion, localeService);
    }

    @Bean
    public RaidRepository getRaidRepository(LocaleService localeService, RaidEntityRepository entityRepository,
                                            PokemonRepository pokemonRepository, GymRepository gymRepository,
                                            ClockService clockService) {
        return new RaidRepository(clockService, localeService, entityRepository, pokemonRepository, gymRepository);
    }

    @Bean
    public PokemonRepository getPokemonRepository(LocaleService localeService) {
        return new PokemonRepository("/mons.json", localeService);
    }

    @Bean
    public PokemonRaidStrategyService getRaidInfoService(PokemonRepository pokemonRepository) {
        return new PokemonRaidStrategyService(pokemonRepository);
    }

    @Bean
    public ConfigRepository getConfigRepository() {
        final HashMap<String, Config> configurationMap = new HashMap<>();
        configurationMap.put("zhorhn tests stuff", new Config("uppsala"));
        configurationMap.put("pokeraidbot_beta", new Config("stockholm"));
        configurationMap.put("pokeraidbot_testing", new Config("skellefteå"));
        return new ConfigRepository(configurationMap);
    }
}
