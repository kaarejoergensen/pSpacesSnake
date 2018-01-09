package org.team08.pspacessnake.GUI;

public class floatTests {
	public static void main(String[] args) {
		
		SnakeStep firstStep = new SnakeStep(0.0, 5.0 , 2.0, 5.0);
		// firstStep.setLine(firstStep.getX1(), firstStep.getY1(), 2.8d, 0.4d);
		SnakeStep oponentStep = new SnakeStep(1.0, 6.0 , 1.0, 4.0);
		
		
		
		
		System.out.println("Første skridt:\t\t" + firstStep);
		System.out.println("Længde:\t\t\t" + firstStep.getLength());
		System.out.println("Tætteste snake Wall:\t" + firstStep.getClosestSnakeWall(oponentStep.getP2(), 5));
		System.out.println("Direkction to point:\t" + firstStep.getDir(oponentStep.getP2()));
		System.out.println("Scale:\t\t\t" + firstStep.getScale(oponentStep.getP2(), 5));
		System.out.println("Do lines intersect:\t" + firstStep.intersectsLine(oponentStep));
	}

}