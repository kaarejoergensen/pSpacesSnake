package org.team08.pspacessnake.Server;

import org.team08.pspacessnake.Model.Player;
import org.team08.pspacessnake.Model.Point;
import org.team08.pspacessnake.Model.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLogic {
    private List<Player> players;
    private boolean isStarted = false;

    public GameLogic() {
        this.players = new ArrayList<>();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        if (this.players == null) {
            this.players = new ArrayList<>();
        }
        this.players.add(player);
        if (this.players.size() > 0) {
            this.isStarted = true;
        }
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

    public void changeDirection(Token token, String direction) {
        for (Player player : players) {
            if (player.getToken().equals(token)) {
                player.setDirection(direction);
                break;
            }
        }
    }

    public List<Player> nextFrame() {
        for (Player player : players) {
            if (!player.getDirection().equals("none")) {
                double angle = player.getAngle();
                if (player.getDirection().equals("left")) {
                    angle += 0.1;
                } else if (player.getDirection().equals("right")) {
                    angle -= 0.1;
                }
                player.setAngle(angle);
            }
            Point position = player.getPosition();
            position = position.translate(0.5 * Math.cos(player.getAngle()), -0.5 * Math.sin(player.getAngle()));
            player.setPosition(position);
        }
        return players;
    }
}