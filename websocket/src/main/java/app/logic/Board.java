package app.logic;

import app.entity.Game;
import app.entity.GamePlayer;
import app.entity.Player;
import app.models.DiscardPile;
import app.models.interfaces.Card;
import app.models.payloads.Action;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Board {
    Logger logger = LoggerFactory.getLogger(Board.class);

    @JsonUnwrapped
    @Getter
    private PlayerManager playerManager;

    @JsonIgnore
    private ActionManager actionManager;

    @Getter
    private DiscardPile discardPile = new DiscardPile();

    @Getter
    @Setter
    private String message;

    @JsonIgnore
    @Getter
    @Setter
    private String privateMessage;

    public Board(PlayerManager playerManager, String message) {
        this.playerManager = playerManager;
        this.actionManager = new ActionManager(this);
        this.message = message;

        prepareCards();
    }

    private void prepareCards() {
        for(Player player : this.playerManager.getPlayers()) {
            player.prepareForGame();
        }
    }

    public void handleTakeCard() {
        if(playerManager.getCurrentPlayer().getHand().amountOfCards() != 10) {
            Card cardDrawn = playerManager.getCurrentPlayer().getDeck().takeCard();
            playerManager.getCurrentPlayer().getHand().addCard(cardDrawn);
            playerManager.getCurrentPlayer().setAmountOfCardsInHand(playerManager.getCurrentPlayer().getHand().amountOfCards());
        } else {
            privateMessage = "You already have 10 cards in your hand, therefore you cant take another card.";
        }
    }

    public void handlePlayCard(Action action) {
        UUID cardId = action.getCardsFromPayload();
        Optional<Card> card = playerManager.getCurrentPlayer().getHand().getCards().stream()
                .filter(c -> c.getId().equals(cardId)).findFirst();

        if(playerManager.getCurrentPlayer().getMana() >= card.get().getMana()) {
            if(playerManager.getCurrentPlayer().getHand().getCards().stream().anyMatch(c -> c.getName().equals(card.get().getName())) && playerManager.getCurrentPlayer().getField().getCards().size() != 5) {
                Optional<Card> playedCard = playerManager.getCurrentPlayer().getHand().getCards().stream().filter(c -> c.getName().equals(card.get().getName())).findFirst();

                if(playedCard.isPresent()) {
                    playerManager.getCurrentPlayer().setMana(playerManager.getCurrentPlayer().getMana() - playedCard.get().getMana());
                    playerManager.getCurrentPlayer().getHand().playCard(playedCard.get());
                    playerManager.getCurrentPlayer().getField().addCard(playedCard.get());
                }
            } else {
                privateMessage = "You already have 5 cards on the field, play them first.";
            }
        } else {
            privateMessage = "You dont have enough mana to play the card.";
        }
    }

    public void handleAttack(Action action, String gameId, GameLogic gameLogic) {
        UUID playerPayload = action.getCardsFromPayload();
        UUID enemyPayload = action.getEnemyCardsFromPayload();

        Optional<Player> enemyPlayer = playerManager.getPlayers().stream()
                .filter(player -> !player.getId().equals(playerManager.getCurrentPlayer().getId())).findFirst();

        Optional<Game> currentGame = gameLogic.findById(UUID.fromString(gameId));

        Optional<Card> playerCard = playerManager.getCurrentPlayer().getField().getCards().stream()
                .filter(c -> c.getId().equals(playerPayload)).findFirst();

        Optional<Card> enemyCard = enemyPlayer.get().getField().getCards().stream()
                .filter(c -> c.getId().equals(enemyPayload)).findFirst();

        if(!enemyCard.isPresent() && playerCard.isPresent() && enemyPlayer.get().getField().getCards().isEmpty() && !playerCard.get().getHasAttacked()) {
            enemyPlayer.get().setHp(enemyPlayer.get().getHp() - playerCard.get().getDamage());
            playerCard.get().setHasAttacked(true);

            if(enemyPlayer.get().isDead() && currentGame.isPresent()) {
                gameLogic.endGame(playerManager.getCurrentPlayer(), currentGame.get());
            }

            return;
        }

        if(enemyCard.isPresent() && playerCard.isPresent() && currentGame.isPresent() && !playerCard.get().getHasAttacked()) {
            if(enemyCard.get().getHealth() >= playerCard.get().getHealth()) {
                int leftHealth = enemyCard.get().getHealth() - playerCard.get().getHealth();

                playerCard.get().setHealth(playerCard.get().getHealth() - enemyCard.get().getHealth());
                enemyCard.get().setHealth(enemyCard.get().getHealth() - playerCard.get().getDamage());

                playerCard.get().setHasAttacked(true);

                if(playerCard.get().isDead()) {
                    playerManager.getCurrentPlayer().getField().removeCard(playerCard.get());
                    discardPile.addCard(playerManager.getCurrentPlayer(), playerCard.get());
                }

                if(enemyCard.get().isDead()) {
                    enemyPlayer.get().getField().removeCard(enemyCard.get());
                    discardPile.addCard(enemyPlayer.get(), enemyCard.get());
                }

                if(leftHealth > 0) {
                    playerManager.getCurrentPlayer().setHp(playerManager.getCurrentPlayer().getHp() - leftHealth);

                    if(playerManager.getCurrentPlayer().getHp() <= 0) {
                        gameLogic.endGame(enemyPlayer.get(), currentGame.get());
                    }
                }
            } else {
                enemyCard.get().setHealth(enemyCard.get().getHealth() - playerCard.get().getDamage());
                playerCard.get().setHealth(playerCard.get().getHealth() - enemyCard.get().getHealth());
                playerCard.get().setHasAttacked(true);



                if(enemyCard.get().isDead()) {
                    enemyPlayer.get().getField().removeCard(enemyCard.get());
                    discardPile.addCard(enemyPlayer.get(), enemyCard.get());
                }
            }
        }
    }
}