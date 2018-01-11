package org.team08.pspacessnake.Model;

public class GameSettings {
	
    private int size = 5;
    private int width = 1000;
    private int height = 1000;
    private int cellSize = 5;
    private int frameRate = 24;
    
    public GameSettings() {	
    }
    
    public GameSettings(int width, int height) {
    	this.setWidth(width);
    	this.setHeight(height);
    }
    
    /**
	 * @return the size
	 */
    public int getSize() {
		return size;
	}
    
    /**
	 * @return the frameRate
	 */
	public int getFrameRate() {
    	return this.frameRate;
    }

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @param width the width to set
	 */
	private void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @param height the heightto set
	 */
	private void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the cellSize
	 */
	public int getCellSize() {
		return cellSize;
	}
}
