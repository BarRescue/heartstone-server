package app.service;

import app.entity.Player;
import app.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
