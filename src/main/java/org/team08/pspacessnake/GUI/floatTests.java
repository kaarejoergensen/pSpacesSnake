package org.team08.pspacessnake.GUI;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;

public class floatTests {

	// Size of game Board in pixels
    private static final int B_HEIGHT = 600;
    private static final int B_WIDTH = 800;
    private static final int BIN_SIZE = 5;

    // Cells of 10x10 pixels
    static int numRow = (int)Math.floor(B_HEIGHT / (double) BIN_SIZE) + 1;
    static int numCol = (int)Math.floor(B_WIDTH / (double) BIN_SIZE) + 1;
    static LinkedList<Point2D.Double>[][] boardCells = (LinkedList<Point2D.Double>[][]) new LinkedList<?>[numRow][numCol];
    static ListIterator<Point2D.Double>[][] boardCellsIterators = (ListIterator<Point2D.Double>[][]) new ListIterator<?>[numRow][numCol]; 
    
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		    
	    // Initialiste 2D arrays
	    for (int row = 0; row < numRow; row++) {
	    	for (int col = 0; col < numCol; col++) {
	    		boardCells[row][col] = new LinkedList<Point2D.Double>();
	    		boardCellsIterators[row][col] = boardCells[row][col].listIterator();
	    	}
	    }
	    
	      
	    // Random points for testing
	    Point2D.Double somePos = new Point2D.Double(0.0, 0.0);
	    Point2D.Double someOtherPos = new Point2D.Double(3.0, 3.0);
	    addPoint(somePos);
	    //addPoint(someOtherPos);
	    
	    System.out.println(checkCollision(someOtherPos));
	    
	    /*// test 
	    boardCellsIterators[6][8].add(somePos);
	    boardCellsIterators[6][8].add(someOtherPos);
	    boardCellsIterators[6][8] = boardCells[6][8].listIterator(); //reset ListIterator
	    System.out.println(boardCellsIterators[6][8].next());
	    System.out.println(boardCellsIterators[6][8].next());
	    System.out.println(boardCellsIterators[6][8].previous());
	    System.out.println(boardCellsIterators[6][8].previous());
	    */
	    
	    
	    
	    	
	}
	
	public static boolean isOnBoard(Point2D.Double point) {
		return (point.getX() < 0d && point.getY() > (double) B_WIDTH && point.getY() < 0d && point.getY() > B_HEIGHT);
	}
	
	public static boolean checkCollision(Point2D.Double point) {
		int pointCellX = (int) point.getX(); //the x coordinate of the bin the point belongs to.
		int pointCellY = (int) point.getY(); //the y coordinate of the bin the point belongs to.
		
		Iterator tempIterator;
		for (int dx = -1; dx <= 1; dx++) {
			if (!(pointCellX + dx >= 0 && pointCellX + dx <= numRow))
				continue;
			for (int dy = -1; dy <= 1; dy++) {
				if (!(pointCellY + dy >= 0 && pointCellY + dy <= numCol))
					continue;
				tempIterator = boardCells[pointCellY + dy][pointCellX + dx].listIterator();
				while (tempIterator.hasNext()) {
					if (point.distance((Point2D.Double)tempIterator.next()) < 5d)
						return true;
				}
					
			}
		}
		return false;
	}
	
    public static void addPoint(Point2D.Double point) {
    	int i = (int) point.getX() / BIN_SIZE;
    	int j = (int) point.getY() / BIN_SIZE;
    	boardCellsIterators[i][j].add(point);
    }
    
}