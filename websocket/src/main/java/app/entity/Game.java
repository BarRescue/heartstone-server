package app.entity;

import app.entity.enums.GameStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "game")
@Getter @Setter
public class Game implements Serializable {
    @Id
    @Type(type="uuid-char")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private GameStatus gameStatus;

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<GamePlayer> players = new HashSet<>();

    public Game() {}

    public Game(UUID id) {
        this.id = id;
        this.gameStatus = GameStatus.WAITING;
    }

    public Game(Player player) {
        this.gameStatus = GameStatus.WAITING;
        this.players.add(new GamePlayer(this, player));
    }

    public void addPlayer(Player player) {
        this.players.add(new GamePlayer(this, player));
    }

    public boolean containsPlayer(Player player) {
        return players.stream().anyMatch(gamePlayer -> gamePlayer.getGame().getId().equals(this.getId()) && gamePlayer.getPlayer().getId().equals(player.getId()));
    }
}
