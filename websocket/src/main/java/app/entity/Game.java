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

    @NotBlank
    private String name;

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

    public Game(String name, Player... players) {
        this.name = name;
        this.gameStatus = GameStatus.WAITING;

        for(Player player : players){
            this.players.add(new GamePlayer(this, player));
        }
    }

    public void addPlayer(Player player) {
        this.players.add(new GamePlayer(this, player));
    }

    public void removePlayer(Player player) {
        for (GamePlayer gamePlayer : this.players)
            if (gamePlayer.getPlayer().getId().equals(player.getId())) {
                gamePlayer.getPlayer().getGames().remove(gamePlayer);
                gamePlayer.setPlayer(null);
                gamePlayer.setGame(null);
            }
    }

    public boolean containsPlayer(Player player) {
        return players.stream().anyMatch(gamePlayer -> gamePlayer.getGame().getId().equals(this.getId()) && gamePlayer.getPlayer().getId().equals(player.getId()));
    }
}
