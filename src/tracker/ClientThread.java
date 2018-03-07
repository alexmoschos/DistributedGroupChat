package tracker;

import common.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {
    private Socket socket;
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    Tracker tracker;
    String ip = null;
    Integer port = null;
    Integer id = null;
    String username;
    public ClientThread(Socket socket, Tracker tracker){
        System.out.println("A new client thread has been created");
        this.socket = socket;
        this.tracker = tracker;
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput  = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException eIO) {
            System.err.println("Exception creating new Input/output Streams: " + eIO);
        }
    }
    public void run() {
        System.out.println("Client thread started");
        try {
            ControlMessage c = (ControlMessage) sInput.readObject();
            if (c.getId() == -1 && c.getType() == ControlMessage.Type.Register) {
                String[] parts = c.getInfo().split(",");
                ip = parts[0];
                port = Integer.parseInt(parts[1]);
                username = parts[2];
                id = tracker.getNextId();
                RegisterReply r = new RegisterReply();
                r.id = id;
                sOutput.writeObject(r);
                UserInfo client = new UserInfo(username,id,ip,port);
                tracker.clients.add(id,client);
            } else if (c.getId() == -1){

            } else {
                id = c.getId();
//                System.out.println(id);
//                System.out.println(tracker.clients);
                UserInfo client = tracker.clients.get(id);
                username = client.username;
                ip = client.ip;
                port = client.port;
                handle(c);
            }
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void handle(ControlMessage c){
        try{
            String name;
            switch(c.getType()){
                case Register:
                    System.out.println("Something wrong happened this client registered twice" + id.toString());
                    break;
                case ListGroups:
                    ListGroupsReply r = new ListGroupsReply();
                    r.groups = new ArrayList<>(tracker.groups.keySet());
                    sOutput.writeObject(r);
                    break;
                case ListMembers:
                    name = c.getInfo();
                    ListMembersReply lmr = new ListMembersReply();
                    lmr.users = new ArrayList<>();
                    if(tracker.groups.containsKey(name)) {
                        for (UserInfo t : tracker.groups.get(name)) {
                            lmr.users.add(t.username);
                        }

                    }
                    sOutput.writeObject(lmr);
                    break;
                case JoinGroup:
                    name = c.getInfo();
                    JoinGroupReply inf = new JoinGroupReply();
                    inf.users = new ArrayList<>();
                    tracker.groups.put(name,new UserInfo(username,id,ip,port));
                    if(tracker.groups.containsKey(name)){
                        inf.users = new ArrayList<>(tracker.groups.get(name));
                    }
                    //should we send the new guy as part of the group
                    sOutput.writeObject(inf);
                    break;
                case ExitGroup:
                    name = c.getInfo();
                    ControlReply ack = new ControlReply();
                    tracker.groups.remove(name,new UserInfo(username,id,ip,port));
                    sOutput.writeObject(ack);
                    break;
                case Quit:
                    for(String key : tracker.groups.keySet()){
                        tracker.groups.remove(key,new UserInfo(username,id,ip,port));
                    }
                    ControlReply ackk = new ControlReply();
                    sOutput.writeObject(ackk);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
