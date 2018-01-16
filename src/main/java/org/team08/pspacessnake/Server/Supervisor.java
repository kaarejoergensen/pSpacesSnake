package org.team08.pspacessnake.Server;

import org.jspace.*;
import org.team08.pspacessnake.Helpers.Utils;
import org.team08.pspacessnake.Model.Room;
import org.team08.pspacessnake.Model.Token;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Supervisor {
    private static final String PROTOCOL = "tcp://";
    private static final String PORT = "9001";
    private static final String TYPE = "?keep";

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        InetAddress address = InetAddress.getLocalHost();
        String host = PROTOCOL + address.getHostAddress() + ":" + PORT + "/" + TYPE;

        SpaceRepository repository = new SpaceRepository();
        repository.addGate(host);
        repository.add("space", new SequentialSpace());

        Space space = repository.get("space");
        space.put("heartbeats", new ArrayList<>());
        System.out.println("Supervisor started!");

        new Thread(new CreateClients(space)).start();
        new Thread(new HeartbeatServer(space)).start();
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

class HeartbeatServer implements Runnable {
    private Space space;

    public HeartbeatServer(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<Object[]> roomsQuery = space.queryAll(new ActualField("room"), new FormalField(String.class),
                        new FormalField(Room.class));
                if (roomsQuery != null) {
                    List<String> URLs = roomsQuery.stream().map(o -> (String) o[1]).collect(Collectors.toList());
                    for (String URL : URLs) {
                        if (space.getp(new ActualField("heartbeat"), new ActualField(URL)) == null) {
                            Object[] room = space.get(new ActualField("room"), new ActualField(URL), new FormalField(Room.class));
                            System.out.println("HEARTBEAT: Removed room " + ((Room)room[2]).getName() + " " + URL);
                        }
                    }
                }
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
