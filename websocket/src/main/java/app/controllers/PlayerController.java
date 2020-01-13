package app.controllers;

import app.entity.Player;
import app.helpers.UrlHelper;
import app.logic.PlayerLogic;
import app.models.DTO.PlayerDTO;
import app.models.DTO.PlayerHighscoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class PlayerController {

    private PlayerLogic playerLogic;

    @Autowired
    public PlayerController(PlayerLogic playerLogic) {
        this.playerLogic = playerLogic;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/highscore")
    public ResponseEntity highscore(HttpServletRequest request) {
        try {
            List<PlayerHighscoreDTO> dto = new ArrayList<>();

            for(Player p : this.playerLogic.getAllPlayers()) {
                dto.add(new PlayerHighscoreDTO(p.getId(), p.getFullName(), p.getGamesWon(), UrlHelper.getCurrentUrl(request)));
            }

            return ok(dto);
        } catch(Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not load users");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/player/{id}")
    public ResponseEntity player(@PathVariable String id) {
        Optional<Player> player = this.playerLogic.findById(UUID.fromString(id));

        if(player.isPresent()) {
            PlayerDTO dto = new PlayerDTO(player.get().getId(), player.get().getFullName(), player.get().getGamesWon());
            return ok(dto);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not load user");
    }
}
