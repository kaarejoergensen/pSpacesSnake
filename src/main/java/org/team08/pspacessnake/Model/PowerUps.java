package org.team08.pspacessnake.Model;

import java.util.concurrent.ThreadLocalRandom;

public class PowerUps {


    private static String[] powers = {"Fast", "Slow", "Big", "Small", "Edge", "Angle", "Clear"};
    private Point position;
    private String power;

    public PowerUps() {
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
