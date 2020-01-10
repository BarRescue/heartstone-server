package config;

import app.service.PlayerService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class PlayerMock {
    @Bean
    @Primary
    public PlayerService playerService() {
        return Mockito.mock(PlayerService.class);
    }
}
