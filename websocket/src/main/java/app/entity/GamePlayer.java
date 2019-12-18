package app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "game_player")
@Getter @Setter
public class GamePlayer implements Serializable {
    @Id
    @Type(type="uuid-char")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID id;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JsonBackReference
    private Game game;

    @ManyToOne(
            fetch = FetchType.EAGER
    )
    @JsonManagedReference
    @JsonUnwrapped
    private Player player;
}
