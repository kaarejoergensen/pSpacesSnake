package org.team08.pspacessnake.Server;

import javafx.scene.paint.Color;
import org.team08.pspacessnake.Model.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

//@SuppressWarnings("restriction")
public class GameLogic {
    private List<Player> players;
    private boolean isStarted = false;
    private GameSettings gameSettings;
	private int numRows;
	private int numCols;
	private LinkedList<Point>[][] boardCells;
	private ListIterator<Point>[][] boardCellsIterators;
	private Color[] colorList = {Color.RED, Color.BLUE, Color.GREEN, Color.WHITE};
	private ArrayList<Powerups> powerups = new ArrayList<Powerups>();
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
	
	public ArrayList<Powerups> getPowerups() {
		return powerups;
	}
	
	public void addPowerup(Powerups powerup) {
		powerups.add(powerup);
	}

	public boolean playerIsOnBoard(Point point) {
		return (0d <= point.getX() && point.getX() <= gameSettings.getWidth() && 0d <= point.getY() && point.getY() <= gameSettings.getHeight());
	}

/*
	public boolean checkCollision(Point point) {
		int pointCellX = getCellX(point); //the x coordinate of the bin the point belongs to.
		int pointCellY = getCellY(point); //the y coordinate of the bin the point belongs to.
		
		
		Point occupiedPoint;
		Iterator<Point> tempIterator;
		for (int dx = -1; dx <= 1; dx++) {
			if (!(pointCellX + dx >= 0 && pointCellX + dx <= numRows))
				continue;
			for (int dy = -1; dy <= 1; dy++) {
				if (!(pointCellY + dy >= 0 && pointCellY + dy <= numCols))
					continue;
				tempIterator = boardCells[pointCellY + dy][pointCellX + dx].listIterator();
				while (tempIterator.hasNext()) {
					occupiedPoint = (Point)tempIterator.next();
					// if (point.distance(occupiedPoint) < point.getRadius() + occupiedPoint.getRadius()) return true;
					if (point.distance(occupiedPoint) < 2.5 + 2.5) return true;
				}
					
			}
		}
		return false;
	}
*/

	public boolean checkCollision(Point point) {
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
	
	public boolean checkBufferedPointsCollision(Player currentPlayer) {
		for (Player thisPlayer : players) {
			if (thisPlayer == currentPlayer) continue;
			for (Point bufferPoint : thisPlayer.getPointBuffer())
				if (currentPlayer.getPosition().distance(bufferPoint) < currentPlayer.getPosition().getRadius() + bufferPoint.getRadius()) {
					return true;
				}
			}
		return false;
	}
	
	public int getCellX(Point point) {
		return (int) point.getX() / gameSettings.getCellSize();
	}

	public int getCellY(Point point) {
		return (int) point.getY() / gameSettings.getCellSize();
	}

	/*
	 * Adds new player position to buffer and releases non-overlapping buffered points to appropriate cell.  
	 */
	public void addPoint(Player currentPlayer) {
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

	public List<Player> nextFrame() {
		for (Player player : players) {
			//if (player.isDead()) continue;	// We might just remove dead player from players ???
			player.turn();
			//player.move(gameSettings.getWidth(), gameSettings.getHeight());
			//player.move();
			Point newPoint = player.move(gameSettings.getWidth(), gameSettings.getHeight());
			// Point newPoint = player.move();
			if (!playerIsOnBoard(newPoint)) {
				player.kill();
				continue;
			}
			addPoint(player);
			//addPoint(player.getPosition());
			// if (checkCollision(newPoint)) player.kill();
			
		}
		return players;
	}

	public void setRemember(Boolean holes, Player player) {
		for(Player player1 : players) {
			if(player == player1) {
				player.setRemember(holes);	
			}
		}

	}

	public Player makePlayer(Token token) {
        Player newPlayer = new Player(token);
        newPlayer.setColor(colorList[ThreadLocalRandom.current().nextInt(0,4)]);
        Point newPoint = new Point(ThreadLocalRandom.current().nextInt(5, gameSettings.getWidth() - 5),
                ThreadLocalRandom.current().nextInt(5, gameSettings.getHeight() - 5), newPlayer.getColor());
        newPlayer.setPosition(newPoint);
        newPlayer.setAngle(newPlayer.getPosition().getAngleToPoint(gameSettings.getWidth() / 2d, gameSettings.getHeight() / 2d));
        System.out.printf("START: [%f, %f]\tAngle: %f rad\t MIDT: [%f, %f] ", newPoint.getX(), newPoint.getY(), newPlayer.getAngle(), gameSettings.getWidth() / 2d, gameSettings.getHeight() / 2d);
        return newPlayer;
    }
}
