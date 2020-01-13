package app.logic;

import app.entity.Player;
import app.jwt.TokenProvider;
import app.models.interfaces.Card;
import app.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerLogic {
    private PlayerService playerService;
    private TokenProvider tokenProvider;

    @Autowired
    public PlayerLogic(PlayerService playerService, TokenProvider tokenProvider) {
        this.playerService = playerService;
        this.tokenProvider = tokenProvider;
    }

    // Initializer for tests
    public PlayerLogic(PlayerService playerService) {
        this.playerService = playerService;
    }

    public Player createOrUpdate(Player player) {
        return this.playerService.createOrUpdate(player);
    }

    public List<Player> getAllPlayers() {
        return this.playerService.getAllPlayers();
    }

    public Optional<Player> findById(UUID id) {
        return this.playerService.findByID(id);
    }

    public Authentication getAuthOnToken(String jwt) {
        String token = this.tokenProvider.resolveToken("Bearer " + jwt);

        if(token != null && this.tokenProvider.validateToken(token)) {
            return this.tokenProvider.getAuthentication(token);
        }

        return null;
    }

    public int getGamesWon(UUID id) {
        return this.playerService.getGamesWonById(id);
    }

    void takeCard(Player player) {
        Card cardDrawn = player.getDeck().takeCard();
        player.getHand().addCard(cardDrawn);
        player.setAmountOfCardsInHand(player.getHand().amountOfCards());
    }

    void playCard(Player player, Card card) {
        player.setMana(player.getMana() - card.getMana());
        player.getHand().playCard(card);

        card.setHasAttacked(true);
        player.getField().addCard(card);
    }

    void attackPlayer(Player player, int damage) {
        player.setHp(player.getHp() - damage);
    }

    void attackCard(Player enemyPlayer, Player currentPlayer, Card playerCard, Card enemyCard) {
        // Init Health and Damage
        final int enemyHealth = enemyCard.getHealth();
        final int playerDamage = playerCard.getDamage();

        // Calculate any leftovers to attack player himself
        int deflectHealth = enemyCard.getHealth() - playerCard.getHealth();
        int attackHealth = playerCard.getDamage() - enemyCard.getHealth();

        // Deflect and attack cards
        enemyCard.attack(playerDamage);
        playerCard.attack(enemyHealth);

        if(attackHealth > 0) {
            attackPlayer(enemyPlayer, attackHealth);
        }

        if(deflectHealth > 0) {
            attackPlayer(currentPlayer, deflectHealth);
        }
    }
}
