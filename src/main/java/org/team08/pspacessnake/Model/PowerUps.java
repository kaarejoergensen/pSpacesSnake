package org.team08.pspacessnake.Model;

import java.util.concurrent.ThreadLocalRandom;

public class PowerUps {


    private static String[] powers = {"Fast","Big", "Edge"};
    private Point position;
    private String power;

    public PowerUps() {
        power = powers[1];
//        power = powers[ThreadLocalRandom.current().nextInt(0, powers.length)];
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
