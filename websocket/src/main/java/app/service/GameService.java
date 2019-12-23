package app.service;

import app.entity.Game;
import app.entity.enums.GameStatus;
import app.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {
    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getWaitingGame() {
        return this.gameRepository.findByStatus(GameStatus.WAITING);
    }

    public List<Game> getStartedGame() {
        return this.gameRepository.findByStatus(GameStatus.STARTED);
    }

    public Game createOrUpdate(Game game) {
        return this.gameRepository.save(game);
    }

    public void deleteGame(UUID id) {
        this.gameRepository.deleteById(id);
    }

    public Optional<Game> findGameByID(UUID id) {
        return this.gameRepository.findById(id);
    }
}
