package org.team08.pspacessnake.Server;

import org.jspace.*;
import org.team08.pspacessnake.Helpers.Utils;
import org.team08.pspacessnake.Model.Player;
import org.team08.pspacessnake.Model.Room;
import org.team08.pspacessnake.Model.Token;

import java.io.IOException;
import java.util.List;

public class Server {
    final static String URI = "tcp://127.0.0.1:9001/";
    private final static String GATE_URI = URI + "?keep";

    public static void main(String[] args) {
        SpaceRepository repository = new SpaceRepository();
        repository.addGate(GATE_URI);
        repository.add("space", new SequentialSpace());

        Space space = repository.get("space");
        System.out.println("Server started!");

        new Thread(new CreateClients(space)).start();
        new Thread(new CreateRooms(repository, space)).start();
    }
}

class CreateClients implements Runnable {
    private Space space;

    CreateClients(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] createClient = space.get(new ActualField("createClient"), new FormalField(String.class));
                String UID = Utils.generateServerUUID(8, space);
                space.put("createClientResult", createClient[1], new Token(UID, (String) createClient[1]));
                System.out.println("New player with name " + createClient[1] + " and UID " + UID + " created.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class CreateRooms implements Runnable {


    private SpaceRepository repository;
    private Space space;

    CreateRooms(SpaceRepository repository, Space space) {
        this.repository = repository;
        this.space = space;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object[] create = space.get(new ActualField("createRoom"), new FormalField(String.class), new
                        FormalField
                        (Token.class));
                String UID = Utils.generateServerUUID(8, space);
                repository.add(UID, new SequentialSpace());

                Room room = new Room(UID, (String) create[1]);
                space.put("room", UID, room);
                new Thread(new Chat(new RemoteSpace(Server.URI + UID + "?keep"))).start();

                space.put("createRoomResult", UID, create[1], create[2]);
                System.out.println("New room with name " + create[1] + " and UID " + UID + " created!");

                GameLogic gameLogic = new GameLogic();
                new Thread(new GameReader(new RemoteSpace(Server.URI + UID + "?keep"), gameLogic)).start();
                new Thread(new GameWriter(new RemoteSpace(Server.URI + UID + "?keep"), gameLogic)).start();

                new Thread(new EnterRoom(space, new RemoteSpace(Server.URI + UID + "?keep"), UID, gameLogic)).start();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
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
        while (true) {
            try {
                if (gameLogic.isStarted()) {
                    float time = System.currentTimeMillis();
                    List<Player> players = gameLogic.nextFrame();
                    for (Player player : players) {
                        for (Player player1 : players) {
                            space.put("Player moved", player.getPosition(), player1.getToken());
                        }
                    }
                    time = System.currentTimeMillis() - time;
                    if (time < 1000.0f / 24) {
                        Thread.sleep((long) (1000.0f / 24 - time));
                    }
                }
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
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
                System.out.println(((Token) direction[2]).getName() + " changed direction to " + direction[1]);
            } catch (InterruptedException e) {
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
                Object[] roomGet = space.getp(new ActualField("room"), new ActualField(UID), new FormalField(Object
                        .class));
                if (roomGet == null) {
                    space.put("enterResult", Boolean.FALSE, enter[3]);
                    continue;
                }
                Room room = (Room) roomGet[2];
                Token token = (Token) enter[2];
                room.addToken(token);
                space.put("room", UID, room);
                space.put("enterResult", Boolean.TRUE, token);
                roomSpace.put("message", "User '" + token.getName() + "' entered room!", new Token("0", "System"));
                roomSpace.put("player", token, new Player(token));
                System.out.println("Added user " + token.getName() + " to room " + UID);

                Player player = new Player(token);
                gameLogic.addPlayer(player);
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
                System.out.println("Got message " + message[1] + " from " + message[2] + ". Sending to " + (users
                        .size() - 1) + " users.");
                users.stream().filter(u -> !u[1].equals(message[2])).forEach(u -> {
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