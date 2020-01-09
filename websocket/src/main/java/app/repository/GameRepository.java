package app.repository;

import app.entity.Game;
import app.entity.enums.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
    @Query("SELECT game FROM Game game WHERE game.gameStatus = :status")
    List<Game> findByStatus(@Param("status") GameStatus status);
}
