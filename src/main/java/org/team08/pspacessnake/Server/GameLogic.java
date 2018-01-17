package org.team08.pspacessnake.Server;

import org.team08.pspacessnake.Model.*;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

//@SuppressWarnings("restriction")
public class GameLogic {
    private List<Player> players;
    private List<Player> startedPlayers;
    private boolean isStarted = false;
    private GameSettings gameSettings;
    private int numRows;
    private int numCols;
    private LinkedList<Point>[][] boardCells;
    private ListIterator<Point>[][] boardCellsIterators;
    private ArrayList<PowerUps> powerups = new ArrayList<PowerUps>();
    private static int i = 0;

    public GameLogic() {
        this.players = new ArrayList<>();
        initiateCellLists();
    }

    public GameLogic(GameSettings gameSettings) {
        this.players = new ArrayList<>();
        this.gameSettings = gameSettings;
        initiateCellLists();
    }

    // Initialiste 2D arrays
    private void initiateCellLists() {

        numRows = (int) Math.floor(gameSettings.getHeight() / (double) gameSettings.getCellSize()) + 1;
        numCols = (int) Math.floor(gameSettings.getWidth() / (double) gameSettings.getCellSize()) + 1;
        boardCells = (LinkedList<Point>[][]) new LinkedList<?>[numRows][numCols];
        boardCellsIterators = (ListIterator<Point>[][]) new ListIterator<?>[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                boardCells[row][col] = new LinkedList<Point>();
                boardCellsIterators[row][col] = boardCells[row][col].listIterator();
            }
        }
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public ArrayList<PowerUps> getPowerups() {
        return powerups;
    }

    public PowerUps makePowerup() {
        PowerUps newPowerup = new PowerUps();
        Point newPoint = new Point(ThreadLocalRandom.current().nextInt(5, gameSettings.getWidth() - 5),
                ThreadLocalRandom.current().nextInt(5, gameSettings.getHeight() - 5), 0);
        newPowerup.setPosition(newPoint);
        powerups.add(newPowerup);

        return newPowerup;
    }

    private boolean playerIsOnBoard(Point point) {
        return (0d <= point.getX() && point.getX() <= gameSettings.getWidth() && 0d <= point.getY() && point.getY() <= gameSettings.getHeight());
    }

    private boolean checkCollision(Point point) {
        int pointCellX = getCellX(point); //the x coordinate of the bin the point belongs to.
        int pointCellY = getCellY(point); //the y coordinate of the bin the point belongs to.

        Iterator<Point> tempIterator;
        for (int dx = -1; dx <= 1; dx++) {
            if (!(pointCellX + dx >= 0 && pointCellX + dx <= numRows))
                continue;
            for (int dy = -1; dy <= 1; dy++) {
                if (!(pointCellY + dy >= 0 && pointCellY + dy <= numCols))
                    continue;
                tempIterator = boardCells[pointCellY + dy][pointCellX + dx].listIterator();
                while (tempIterator.hasNext()) {
                    if (point.distance((Point) tempIterator.next()) < 5d)
                        return true;
                }

            }
        }
        return false;
    }

    private boolean checkBufferedPointsCollision(Player currentPlayer) {
        for (Player thisPlayer : players) {
            if (thisPlayer == currentPlayer) continue;
            for (Point bufferPoint : thisPlayer.getPointBuffer())
                if (currentPlayer.getPosition().distance(bufferPoint) < currentPlayer.getPosition().getRadius() + bufferPoint.getRadius()) {
                    return true;
                }
        }
        return false;
    }

    private PowerUps hitPowerUp(Player player) {
        for (PowerUps thisPowerUp : powerups) {
            if (player.getPosition().distance(thisPowerUp.getPosition()) < player.getPosition().getRadius() + 10) {
                return thisPowerUp;
            }
        }
        return null;
    }

    private int getCellX(Point point) {
        return (int) point.getX() / gameSettings.getCellSize();
    }

    private int getCellY(Point point) {
        return (int) point.getY() / gameSettings.getCellSize();
    }

    /*
     * Adds new player position to buffer and releases non-overlapping buffered points to appropriate cell.
     */
    private void addPoint(Player currentPlayer) {
        Point playerPos = currentPlayer.getPosition();
        for (Point bufferPoint : currentPlayer.getPointBuffer()) {
            if (playerPos.distance(bufferPoint) > playerPos.getRadius() + bufferPoint.getRadius()) {
                boardCellsIterators[getCellY(bufferPoint)][getCellX(bufferPoint)].add(bufferPoint);
                currentPlayer.getPointBuffer().remove(bufferPoint);
            }
        }
        currentPlayer.getPointBuffer().add(currentPlayer.getPosition());
    }

    public GameSettings getGameSettings() {
        return this.gameSettings;
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

    public void changeDirection(Token token, String direction) {
        for (Player player : players) {
            if (player.getToken().equals(token)) {
                player.setDirection(direction);
                break;
            }
        }
    }

    public void collisionPowerUp(Player player, PowerUps power) {
        ScheduledThreadPoolExecutor execute = new ScheduledThreadPoolExecutor(1);
        switch (power.getPower()) {
            case "Fast":
                player.setSpeed(player.getSpeed() * 2);
                execute.schedule(() -> player.setSpeed(player.getSpeed() / 2), 4, TimeUnit.SECONDS);
                break;
            case "Big":
                player.getPosition().setRadius(player.getPosition().getRadius() * 2);
                execute.schedule(() -> player.getPosition().setRadius(player.getPosition().getRadius() / 2), 4, TimeUnit.SECONDS);
                break;
            case "Angel":
                double startAngle = player.getDAngle();
                player.setDAngle(Math.PI / 2);
                execute.schedule(() -> player.setDAngle(startAngle), 4, TimeUnit.SECONDS);
                break;
            case "Edge":
                player.setEdgeJumper(true);
                execute.schedule(() -> {
                    player.setEdgeJumper(false);
                }, 8, TimeUnit.SECONDS);
                break;
        }
    }


    public List<Player> nextFrame() {
        for (Player player : players) {
            player.turn();
            player.move(gameSettings.getWidth(), gameSettings.getHeight());
            if (!playerIsOnBoard(player.getPosition())) {
                player.kill();
                continue;
            }
            if (player.getRemember()) {
                addPoint(player);
                player.getPosition().setRadius(2.5d);
            } else {
                player.getPosition().setHole(true);
            }
            if (checkCollision(player.getPosition()) || checkBufferedPointsCollision(player)) player.kill();
            PowerUps powerUps = hitPowerUp(player);
            if (powerUps != null && player.getPosition().getPowerUps() == null) {
                collisionPowerUp(player, powerUps);
                player.getPosition().setPowerUps(powerUps);
                powerups.remove(powerUps);
            } else {
                player.getPosition().setPowerUps(null);
            }
        }
        return players;
    }

    public Player makePlayer(Token token) {
        Player newPlayer = new Player(token);
        Point newPoint = new Point(ThreadLocalRandom.current().nextInt(5, gameSettings.getWidth() - 5),
                ThreadLocalRandom.current().nextInt(5, gameSettings.getHeight() - 5), players.size());
        newPlayer.setPosition(newPoint);
        newPlayer.setAngle(newPlayer.getPosition().getAngleToPoint(gameSettings.getWidth() / 2d, gameSettings.getHeight() / 2d));
        System.out.printf("START: [%f, %f]\tAngle: %f rad\t MIDT: [%f, %f] ", newPoint.getX(), newPoint.getY(), newPlayer.getAngle(), gameSettings.getWidth() / 2d, gameSettings.getHeight() / 2d);
        return newPlayer;
    }

    public List<Player> getStartedPlayers() {
        return startedPlayers;
    }

    public void setStartedPlayers() {
        startedPlayers = new ArrayList<>(players);
    }
}
