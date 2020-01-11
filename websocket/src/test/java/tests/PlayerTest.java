package tests;

import app.entity.Player;
import app.logic.PlayerLogic;
import app.repository.PlayerRepository;
import app.service.PlayerService;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerTest {

    @InjectMocks
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        lenient().when(playerRepository.findById(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"))).thenReturn(Optional.of(new Player(UUID.randomUUID(), "Rens Manders", 0)));
        lenient().when(playerRepository.findById(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"))).thenReturn(Optional.of(new Player(UUID.randomUUID(), "Piet Jan", 0)));
        lenient().when(playerRepository.save(Mockito.any(Player.class)))
                .thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void getUsername() {
        Optional<Player> playerOne = playerService.findByID(UUID.fromString("123e4567-e89b-42d3-a456-556642440000"));
        Optional<Player> playerTwo = playerService.findByID(UUID.fromString("123e4567-e89b-42d3-a456-556642440001"));

    }
}