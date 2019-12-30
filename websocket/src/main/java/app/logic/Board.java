package app.logic;

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
        List<Card> cards = action.getCardsFromPayload();

        if(playerManager.getCurrentPlayer().getMana() >= cards.get(0).getMana()) {
            if(playerManager.getCurrentPlayer().getHand().getCards().stream().anyMatch(card -> card.getName().equals(cards.get(0).getName())) && playerManager.getCurrentPlayer().getField().getCards().size() != 5) {
                Optional<Card> playedCard = playerManager.getCurrentPlayer().getHand().getCards().stream().filter(c -> c.getName().equals(cards.get(0).getName())).findFirst();

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

    public void handleAttack(Action action) {
        List<Card> playerPayload = action.getCardsFromPayload();
        List<Card> enemyPayload = action.getEnemyCardsFromPayload();

        Optional<Player> enemyPlayer = playerManager.getPlayers().stream()
                .filter(player -> !player.getId().equals(playerManager.getCurrentPlayer().getId())).findFirst();

        if(playerManager.getCurrentPlayer().getField().getCards().stream().anyMatch(card -> card.getName().equals(playerPayload.get(0).getName())) && enemyPlayer.isPresent() && enemyPayload.isEmpty() && enemyPlayer.get().getField().getCards().isEmpty()) {
            Optional<Card> playerCard = playerManager.getCurrentPlayer().getField().getCards().stream().filter(card -> card.getName().equals(playerPayload.get(0).getName())).findFirst();

            if(playerCard.isPresent() && !playerCard.get().getHasAttacked()) {
                enemyPlayer.get().setHp(enemyPlayer.get().getHp() - playerPayload.get(0).getDamage());
                playerCard.get().setHasAttacked(true);
            }
            return;
        }

        if (enemyPlayer.isPresent() && playerManager.getCurrentPlayer().getField().getCards().stream().anyMatch(card -> card.getName().equals(playerPayload.get(0).getName()))
                && enemyPlayer.get().getField().getCards().stream().anyMatch(card -> card.getName().equals(enemyPayload.get(0).getName()))) {

            Optional<Card> playerCard = playerManager.getCurrentPlayer().getField().getCards().stream().filter(card -> card.getName().equals(playerPayload.get(0).getName())).findFirst();
            Optional<Card> enemyCard = enemyPlayer.get().getField().getCards().stream().filter(card -> card.getName().equals(enemyPayload.get(0).getName())).findFirst();

            if (playerCard.isPresent() && enemyCard.isPresent()) {
                int enemyCardDefence = enemyCard.get().getHealth();

                int playerCardHealth = playerCard.get().getHealth();
                int playerCardAttack = playerCard.get().getDamage();

                if(playerCardAttack <= enemyCardDefence) {
                    enemyCard.get().setHealth(enemyCard.get().getHealth() - playerCardAttack);

                    if(enemyCard.get().isDead()) {
                        enemyPlayer.get().getField().removeCard(enemyCard.get());
                        this.discardPile.addCard(enemyPlayer.get(), enemyCard.get());
                    }
                } else {
                    int healthLeft = playerCardAttack - enemyCard.get().getHealth();
                    enemyCard.get().setHealth(enemyCard.get().getHealth() - playerCardAttack);
                    enemyPlayer.get().setHp(enemyPlayer.get().getHp() - healthLeft);

                    if(enemyCard.get().isDead()) {
                        enemyPlayer.get().getField().removeCard(enemyCard.get());
                        this.discardPile.addCard(enemyPlayer.get(), enemyCard.get());
                    }
                }

                if(enemyCardDefence <= playerCardHealth) {
                    playerCard.get().setHealth(playerCardHealth - enemyCardDefence);

                    if(playerCard.get().isDead()) {
                        playerManager.getCurrentPlayer().getField().removeCard(playerCard.get());
                        this.discardPile.addCard(playerManager.getCurrentPlayer(), playerCard.get());
                    }
                } else {
                    int healthLeft = enemyCardDefence - playerCardHealth;
                    playerCard.get().setHealth(playerCardHealth - enemyCardDefence);
                    playerManager.getCurrentPlayer().setHp(playerManager.getCurrentPlayer().getHp() - healthLeft);

                    if(playerCard.get().isDead()) {
                        playerManager.getCurrentPlayer().getField().removeCard(playerCard.get());
                        this.discardPile.addCard(playerManager.getCurrentPlayer(), playerCard.get());
                    }
                }
            }
        }
    }
}