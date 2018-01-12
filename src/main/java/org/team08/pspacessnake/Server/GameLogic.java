package org.team08.pspacessnake.Server;

import org.team08.pspacessnake.Model.Player;
import org.team08.pspacessnake.Model.Point;
import org.team08.pspacessnake.Model.Token;
import org.team08.pspacessnake.Model.GameSettings;


import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GameLogic {
	private List<Player> players;
	private boolean isStarted = false;
	private static GameSettings gameSettings;
	private static int numRows;
	private static int numCols;
	private static LinkedList<Point>[][] boardCells;
	private static ListIterator<Point>[][] boardCellsIterators;
	private static Color[] colorList = {new Color(1,0,0,1),new Color(1,1,1, 1),new Color(0,0,1, 1)};
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
    	
    	numRows = (int)Math.floor(gameSettings.getHeight() / (double) gameSettings.getCellSize()) + 1;
    	numCols = (int)Math.floor(gameSettings.getWidth() / (double) gameSettings.getCellSize()) + 1;
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
        return players;
    }
    
	public static boolean playerIsOnBoard(Point point) {
		return (point.getX() < 0d && point.getY() > (double) gameSettings.getWidth() && point.getY() < 0d && point.getY() > gameSettings.getHeight());
	}
	
	public static boolean checkCollision(Point point) {
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
					if (point.distance((Point)tempIterator.next()) < 5d)
						return true;
				}
					
			}
		}
		return false;
	}
	
	public static int getCellX(Point point) {
		return (int) point.getX() / gameSettings.getCellSize();
	}
	
	public static int getCellY(Point point) {
		return (int) point.getY() / gameSettings.getCellSize();
	}
	
	/*
	 * Adds new player position point to appropriate cell.
	 */
    public static void addPoint(Point point) {
    	boardCellsIterators[getCellY(point)][getCellX(point)].add(point);
    }
    
    public GameSettings getGameSettings() {
    	return this.gameSettings;
    }

    public void addPlayer(Player player) {
        if (this.players == null) {
            this.players = new ArrayList<>();
        }
		this.players.add(player);
        player.setColor(colorList[i]);
        if (this.players.size() > 0) {
			this.isStarted = true;
        } else {
        	System.out.print("Waiting for more players");
        }
        i++;
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
			player.turn();
			addPoint(player.move());
		}
		return players;
	}
	
	public void setRemember(Boolean holes) {
		players.get(0).setRemember(holes);
	}
}