package org.team08.pspacessnake.GUI;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;

public class floatTests {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		// Size og game Board in pixels
	    final int B_HEIGHT = 600;
	    int B_WIDTH = 800;
	    final int BIN_SIZE = 5;
	    
	    // Cells of 10x10 pixels
	    int numRow = (int)Math.floor(B_HEIGHT / (double) BIN_SIZE) + 1;
	    int numCol = (int)Math.floor(B_WIDTH / (double) BIN_SIZE) + 1;
	    LinkedList<Point2D.Double>[][] boardCells = (LinkedList<Point2D.Double>[][]) new LinkedList<?>[numRow][numCol];
	    ListIterator<Point2D.Double>[][] boardCellsIterators = (ListIterator<Point2D.Double>[][]) new ListIterator<?>[numRow][numCol]; 
	    
	    // Initialiste 2D arrays
	    for (int row = 0; row < numRow; row++) {
	    	for (int col = 0; col < numCol; col++) {
	    		boardCells[row][col] = new LinkedList<Point2D.Double>();
	    		boardCellsIterators[row][col] = boardCells[row][col].listIterator();
	    	}
	    }
	    
	    

	    
	    // Random points for testing
	    Point2D.Double somePos = new Point2D.Double(3.4d, 5.1d);
	    Point2D.Double someOtherPos = new Point2D.Double(0.2d, 2.3d);
	    
	    // test 
	    boardCellsIterators[6][8].add(somePos);
	    boardCellsIterators[6][8].add(someOtherPos);
	    boardCellsIterators[6][8] = boardCells[6][8].listIterator(); //reset ListIterator
	    System.out.println(boardCellsIterators[6][8].next());
	    System.out.println(boardCellsIterators[6][8].next());
	    System.out.println(boardCellsIterators[6][8].previous());
	    System.out.println(boardCellsIterators[6][8].previous());
	
	}
	
    public void savePoint(Point2D.Double point) {
    	int i = (int) point.getX() / BIN_SIZE;
    	int j = (int) point.getY() / BIN_SIZE;
    	boardCellsIterators[i][j].add(point);
    }
}