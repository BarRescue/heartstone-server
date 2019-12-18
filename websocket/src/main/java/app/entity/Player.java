package app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import interfaces.ICard;
import lombok.Getter;
import lombok.Setter;
import models.Card;
import org.hibernate.annotations.Type;


import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "player")
@Getter @Setter
public class Player implements Serializable {
    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private UUID id;

    @NotBlank
    @Setter
    private String fullName;

    @Transient
    @JsonIgnore
    private transient List<Card> cards = new ArrayList<>();

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "player",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonBackReference
    @Getter
    private Set<GamePlayer> games = new HashSet<>();
}
