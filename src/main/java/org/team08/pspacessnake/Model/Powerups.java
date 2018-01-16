package org.team08.pspacessnake.Model;

import java.util.concurrent.ThreadLocalRandom;

public class Powerups {


	private static String[] powers = {"Fast"};
    private Point position;
    private String power;
	public Powerups() {
		power = powers[ThreadLocalRandom.current().nextInt(0, powers.length)];
	}
	public Point getPosition() {
		return position;
	}
	public void setPosition(Point position) {
		this.position = position;
	}
	public String getPower() {
		return power;
	}

}
