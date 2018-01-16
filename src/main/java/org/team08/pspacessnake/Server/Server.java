package org.team08.pspacessnake.Server;

import org.jspace.*;
import org.team08.pspacessnake.Helpers.Utils;
import org.team08.pspacessnake.Model.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Server {
    private static final String PROTOCOL = "tcp://";
    private static final String TYPE = "?keep";

    private String hostURL;
    private SpaceRepository repository;
    private Space space;

    public Server(Space space) throws IOException {
        InetAddress address = InetAddress.getLocalHost();
        hostURL = address.getHostAddress();
        String host = PROTOCOL + hostURL + "/" + TYPE;

        repository = new SpaceRepository();
        repository.addGate(host);

        this.space = space;
    }

    public Room createRoom(String name) throws IOException, InterruptedException {
        String UID = Utils.generateServerUUID(8, space);
        repository.add(UID, new SequentialSpace());

        String roomURL = PROTOCOL + hostURL + "/" + UID + TYPE;
        Room room = new Room(roomURL, name);
        space.put("room", roomURL, room);

        GameSettings gameSettings = new GameSettings(1000, 800);
        GameLogic gameLogic = new GameLogic(gameSettings);
        new RemoteSpace(roomURL).put("Game started", false);
        new Thread(new Chat(new RemoteSpace(roomURL))).start();
        new Thread(new GameReader(new RemoteSpace(roomURL), gameLogic)).start();
        new Thread(new GameWriter(new RemoteSpace(roomURL), gameLogic)).start();
        new Thread(new EnterRoom(space, new RemoteSpace(roomURL), roomURL, gameLogic)).start();
        new Thread(new StartGame(new RemoteSpace(roomURL), gameLogic)).start();
        new Thread(new PowerUp(new RemoteSpace(roomURL), gameLogic)).start();
        new Thread(new HeartbeatClient(space, roomURL)).start();

        System.out.println("New room with name " + name + " and UID " + UID + " created!");
        return room;
    }
}

class PowerUp implements Runnable {
    private Space space;
    private GameLogic gameLogic;

    PowerUp(Space space, GameLogic gameLogic) {
        this.space = space;
        this.gameLogic = gameLogic;


    }

    @Override
    public void run() {
        while (true) {
            if (gameLogic.isStarted()) {
                try {
                    Thread.sleep((long) (7000));
                } catch (InterruptedException e) {
                }
                Powerups newPowerup = new Powerups();
                gameLogic.addPowerup(newPowerup);

                	/*if (player.isDead()) {
                		//System.out.printf("Player: %s is dead\n", player.getToken().getName());
                		//continue;	// don't send new coordinates for dead players.
                	}*/
                for (Player player : gameLogic.getPlayers()) {
                    try {
                        space.put("New Powerup", newPowerup, player.getToken());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }

    }
}

class GameWriter implements Runnable {
    private Space space;
    private GameLogic gameLogic;

    GameWriter(Space space, GameLogic gameLogic) {
        this.space = space;
        this.gameLogic = gameLogic;
    }

    @Override
    public void run() {
        int frameRate = gameLogic.getGameSettings().getFrameRate();
        boolean firstRun = true;
        while (true) {
            try {
                if (gameLogic.isStarted()) {

                    float time = System.currentTimeMillis();
                    List<Player> players = gameLogic.nextFrame();
                    for (Player player : players) {
                    	/*if (player.isDead()) {
                    		//System.out.printf("Player: %s is dead\n", player.getToken().getName());
                    		//continue;	// don't send new coordinates for dead players.
                    	}*/
                        for (Player player1 : players) {
                            if (player1.isDead()) {
                                space.put("message", "Player '" + player.getToken().getName() + "' died!",
                                        new Token("0", "System"));
                            } else {
                                space.put("Player moved", player, player1.getToken());
                            }
                        }
                    }
                    time = System.currentTimeMillis() - time;
                    if (time < 1000.0f / frameRate) {
                        Thread.sleep((long) (1000.0f / frameRate - time));
                    }
                    if (firstRun) {
                        Thread.sleep(2000);
                        firstRun = false;
                    }
                } else {
                    Thread.sleep(100);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }
}

class SetHoles implements Runnable {
    private GameLogic logic;
    private Player player;

    SetHoles(GameLogic logic, Player player) {
        this.logic = logic;
        this.player = player;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int randomNum = ThreadLocalRandom.current().nextInt(1000, 5000);
                Thread.sleep((long) (randomNum));
                logic.setRemember(false, player);
                Thread.sleep((long) (300));
                logic.setRemember(true, player);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class StartGame implements Runnable {
    private Space space;
    private GameLogic gameLogic;

    public StartGame(Space space, GameLogic gameLogic) {
        this.space = space;
        this.gameLogic = gameLogic;
    }

    @Override
    public void run() {
        try {
            while (!gameLogic.isStarted()) {
                Thread.sleep(300);
                List<Object[]> ready = space.queryAll(new ActualField("player"), new FormalField(Token.class),
                        new FormalField(Player.class));
                List<Player> players = ready.stream().map(o -> (Player) o[2]).collect(Collectors.toList());
                gameLogic.setStarted(players.stream().allMatch(Player::isReady) &&
                        players.size() == gameLogic.getPlayers().size() && players.size() > 0);
            }
            space.get(new ActualField("Game started"), new ActualField(false));
            space.put("Game started", true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class GameReader implements Runnable {
    private Space space;
    private GameLogic gameLogic;

    GameReader(Space space, GameLogic gamelogic) {
        this.space = space;
        this.gameLogic = gamelogic;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] direction = space.get(new ActualField("Changed direction"), new FormalField(String.class),
                        new FormalField(Token.class));
                gameLogic.changeDirection((Token) direction[2], (String) direction[1]);
                //System.out.println(((Token) direction[2]).getName() + " changed direction to " + direction[1]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

class EnterRoom implements Runnable {
    private Space space;
    private Space roomSpace;
    private String UID;
    private GameLogic gameLogic;

    EnterRoom(Space space, Space remoteSpace, String UID, GameLogic gameLogic) {
        this.space = space;
        this.roomSpace = remoteSpace;
        this.UID = UID;
        this.gameLogic = gameLogic;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] enter = space.get(new ActualField("enter"), new ActualField(UID),
                        new FormalField(Token.class));
                space.get(new ActualField("roomLock"));
                Object[] roomGet = space.get(new ActualField("room"), new ActualField(UID), new FormalField(Object
                        .class));
                Room room = (Room) roomGet[2];
                Token token = (Token) enter[2];
                room.addToken(token);
                space.put("room", UID, room);
                space.put("roomLock");
                space.put("enterResult", Boolean.TRUE, token);
                Player newPlayer = gameLogic.makePlayer(token);
                roomSpace.put("player", token, newPlayer);
                roomSpace.put("message", "User '" + token.getName() + "' entered room!", new Token("0", "System"));
                System.out.println("Added user " + token.getName() + " to room " + UID);

                gameLogic.addPlayer(newPlayer);
                new Thread(new SetHoles(gameLogic, newPlayer)).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Chat implements Runnable {
    private Space space;

    public Chat(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] message = space.get(new ActualField("message"), new FormalField(String.class), new
                        FormalField(Token.class));
                List<Object[]> users = space.queryAll(new ActualField("player"), new FormalField(Token.class), new
                        FormalField(Player.class));
                System.out.println("Got message " + message[1] + " from " + message[2] + ". Sending to " + users
                        .size() + " users.");
                users.forEach(u -> {
                    try {
                        space.put("message" + ((Token) u[1]).getID(), message[1], ((Token) message[2]).getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class HeartbeatClient implements Runnable {
    private Space space;
    private String roomURL;

    public HeartbeatClient(Space space, String roomURL) {
        this.space = space;
        this.roomURL = roomURL;
    }

    @Override
    public void run() {
        while (true) {
            try {
                space.get(new ActualField("roomLock"));
                Object[] roomGet = space.get(new ActualField("room"), new ActualField(roomURL), new FormalField(Room.class));
                Room room = (Room) roomGet[2];
                room.setHeartbet(System.currentTimeMillis());
                space.put("room", roomURL, room);
                space.put("roomLock");
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
