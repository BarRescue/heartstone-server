package app.logic;

import app.entity.Game;
import app.entity.Player;
import app.models.DiscardPile;
import app.models.interfaces.Card;
import app.models.payloads.Action;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

public class Board {
    @JsonUnwrapped
    @Getter
    private StateManager stateManager;

    @Getter
    private DiscardPile discardPile = new DiscardPile();

    @Getter
    @Setter
    private String message;

    @JsonIgnore
    @Getter
    @Setter
    private String privateMessage;

    @JsonIgnore
    private PlayerLogic playerLogic;

    @JsonIgnore
    private GameLogic gameLogic;

    Board(StateManager stateManager, PlayerLogic playerLogic, GameLogic gameLogic) {
        this.stateManager = stateManager;
        this.message = "It's " + stateManager.getCurrentPlayer().getFullName() + "'s turn.";
        this.playerLogic = playerLogic;
        this.gameLogic = gameLogic;
    }

    public void handleNextTurn() {
        this.stateManager.getNextPlayer();
        this.message = "It's " + this.stateManager.getCurrentPlayer().getFullName() + "'s turn.";
    }

    public boolean handleTakeCard() {
        Player currentPlayer = stateManager.getCurrentPlayer();

        // Check if play already has 10 cards in hand
        if(currentPlayer.getHand().hasMaxCards()) {
            privateMessage = "You already have 10 cards in your hand!";
            return false;
        }

        playerLogic.takeCard(currentPlayer);
        return true;
    }

    public boolean handlePlayCard(Action action) {
        Player currentPlayer = stateManager.getCurrentPlayer();
        Card card = currentPlayer.getHand().findCardById(action.getCardsFromPayload());

        // Check if Card is found and if player has enough mana
        if(card != null && currentPlayer.hasEnoughMana(card.getMana())) {
            // Check if card exists in hand and if field already has 5 cards
            if(!currentPlayer.getField().hasMaxCards()) {
                playerLogic.playCard(currentPlayer, card);
            } else {
                privateMessage = "You already have 5 cards on the field, play them first.";
                return false;
            }
        } else {
            privateMessage = "You dont have enough mana to play the card.";
            return false;
        }

        return true;
    }

    public boolean handleAttack(Action action, Game game) {
        Player currentPlayer = stateManager.getCurrentPlayer();
        Player enemyPlayer = stateManager.getEnemyPlayer();

        Card playerCard = currentPlayer.getField().findCardById(action.getCardsFromPayload());
        Card enemyCard = enemyPlayer.getField().findCardById(action.getEnemyCardsFromPayload());

        // Attack on player
        if(enemyPlayer.getField().getCards().isEmpty() && !playerCard.getHasAttacked()) {
            playerLogic.attackPlayer(enemyPlayer, playerCard.getDamage());

            playerCard.setHasAttacked(true);

            this.message = playerCard.getName() + " attacked " + enemyPlayer.getFullName();

            // Check if enemy player is dead
            checkIfPlayerIsDead(enemyPlayer, currentPlayer, game);

            return true;
        }

        // Attack on card
        if(!enemyPlayer.getField().getCards().isEmpty() && !playerCard.getHasAttacked()) {
            playerLogic.attackCard(enemyPlayer, currentPlayer, playerCard, enemyCard);

            playerCard.setHasAttacked(true);

            this.message = playerCard.getName() + " attacked " + enemyCard.getName();

            // Check if anything is dead
            checkIfCardIsDead(playerCard, currentPlayer);
            checkIfCardIsDead(enemyCard, enemyPlayer);
            checkIfPlayerIsDead(currentPlayer, enemyPlayer, game);

            return true;
        }

        this.privateMessage = "You already have attacked with card: " + playerCard.getName();
        return false;
    }

    private void checkIfCardIsDead(Card card, Player player) {
        if(card.isDead()) {
            player.getField().removeCard(card);
            discardPile.addCard(player, card);
        }
    }

    private void checkIfPlayerIsDead(Player playerToCheck, Player oppositePlayer, Game game) {
        if(playerToCheck.isDead()) {
            message = playerToCheck.getFullName() + " is dead, " + oppositePlayer.getFullName() + " won the game";
            gameLogic.endGame(oppositePlayer, game);
        }
    }
}