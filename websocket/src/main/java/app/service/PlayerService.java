package app.service;

import app.entity.Player;
import app.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Optional<Player> findByID(UUID id) {
        return this.playerRepository.findById(id);
    }

    public Player createOrUpdate(Player player) {
        return this.playerRepository.save(player);
    }

    public int getGamesWonById(UUID id) {
        Optional<Player> player = this.playerRepository.findById(id);
        return player.map(Player::getGamesWon).orElse(0);
    }

    public List<Player> getAllPlayers() {
        return this.playerRepository.findAll();
    }
}
