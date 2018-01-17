package org.team08.pspacessnake.Model;

import java.util.concurrent.ThreadLocalRandom;

public class PowerUps {


    //private static String[] powers = {"Fast","Big", "Edge", "Angle"};
    private static String[] powers = {"Angle"};
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
