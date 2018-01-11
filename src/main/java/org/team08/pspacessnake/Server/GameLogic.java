package org.team08.pspacessnake.Server;

import org.team08.pspacessnake.Model.Player;
import org.team08.pspacessnake.Model.Token;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private List<Player> players = new ArrayList<>();
    private boolean isStarted = false;

    public GameLogic(List<Player> players, boolean isStarted) {
        this.players = players;
        this.isStarted = isStarted;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        if (this.players == null) {
            this.players = new ArrayList<>();
        }
        this.players.add(player);
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public void startGame() {
        this.isStarted = true;
    }
}
