package org.team08.pspacessnake.GUI;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import org.team08.pspacessnake.GUI.logic.Point;
import org.team08.pspacessnake.GUI.logic.Snake;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


@SuppressWarnings("restriction")
public class SpaceGui extends Application {
	private Space space;
	private int rows;
	private int cols;
	public final static int SIZE = 5;
	private final static String REMOTE_URI = "tcp://127.0.0.1:9001/";
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 1000;
	private static GraphicsContext context;
	private boolean keyPressed = false;
	private List<Point> points;
	
	public void start(Stage primaryStage) throws Exception {
		points = new LinkedList<>();
		StackPane root = new StackPane();
		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		context = canvas.getGraphicsContext2D();
		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(e -> {
			if (keyPressed) {
                return;
            }
			switch (e.getCode()) {
			case LEFT:

				try {
					space.put("LeftKey", true);
					setKeyIsPresset(true);
				} catch (InterruptedException e1) {}
				break;
			case RIGHT:
				try {
					space.put("RightKey", false);
					setKeyIsPresset(true);
				} catch (InterruptedException e1) {}
				break;
			default:
				break;
			}
		});
		canvas.setOnKeyReleased(e -> {
			if (keyPressed) {
                return;
            }
			switch (e.getCode()) {
			case LEFT:
				try {
					space.put("LeftKey", false);
					setKeyIsPresset(false);
				} catch (InterruptedException e1) {}
				break;
			case RIGHT:
				try {
					space.put("LeftKey", false);
					setKeyIsPresset(false);
				} catch (InterruptedException e1) {}
				break;
			default:
				break;

			}
		});


		root.getChildren().add(canvas);

		Scene scene = new Scene(root);
		space = new RemoteSpace(REMOTE_URI + "space?keep");
		rows = (int) WIDTH / SIZE;
		cols = (int) HEIGHT / SIZE;

		

		reset();

		primaryStage.setResizable(false);
		primaryStage.setTitle("Snake");
		primaryStage.setOnCloseRequest(e -> System.exit(0));
		primaryStage.setScene(scene);
		primaryStage.show();


		new Thread(new UpdateGuiThread(space,this)).start();

	}

	public void updateGui(Point point) {
		points.add(point);
		reset();

	}

	public void reset() {
		context.setFill(new Color(0.1, 0.1, 0.1, 1));
		context.fillRect(0, 0, WIDTH, HEIGHT);
		context.setFill(Color.CORNSILK);
		for (int i = 0; i < points.size(); i++) {
			context.fillOval(points.get(i).getX(), points.get(i).getY(), SIZE, SIZE);
		}
	}

	public void startingPositions(List<Point> startPosition) {
		for(int i = 0; i < startPosition.size(); i++ ) {
			Point point = startPosition.get(i);
			points.add(point);
		}
		reset();
	}

	public List<Point> getPoints() {
		return points;
	}
	
	private void setKeyIsPresset(boolean keyPressed) {
		this.keyPressed = keyPressed;
	}

	public static void main(String[] args) {

		Application.launch(args);
	}
}

class UpdateGuiThread implements Runnable {
	private Space space;
	private SpaceGui gui;

	public UpdateGuiThread(Space space, SpaceGui gui) {
		this.space = space;
		this.gui = gui;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Object[] newPoint = space.get(new ActualField("Player moved"), new FormalField(String.class), 
						new FormalField(Point.class));

				gui.updateGui((Point) newPoint[2]); 

			} catch (InterruptedException e) {}
		}

	}
}
