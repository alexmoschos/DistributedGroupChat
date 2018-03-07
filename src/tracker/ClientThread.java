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
    boolean registered;
    Tracker tracker;
    String ip = null;
    Integer port = null;
    Integer id = null;
    String username;
    public ClientThread(Socket socket, Tracker tracker){
        System.out.println("A new client has been created");
        this.socket = socket;
        this.tracker = tracker;

        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput  = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException eIO) {
            System.err.println("Exception creating new Input/output Streams: " + eIO);
        }
        registered = false;
    }
    public void run() {
        System.out.println("Client thread started");
        try {
            while(true){
                ControlMessage c = (ControlMessage) sInput.readObject();
                if(c.getType() == ControlMessage.Type.Register){
                    registered = true;
                    String[] parts = c.getInfo().split(",");
                    ip = parts[0];
                    port = Integer.parseInt(parts[1]);
                    username = parts[2];
                    id = tracker.getNextId();
                    RegisterReply r = new RegisterReply();
                    r.id = id;
                    sOutput.writeObject(r);
                    break;
                }
            }
            System.out.println("My new id is " + id.toString());
            // we only enter here when the other guy is registered
            String name = "";
            while(true) {
                ControlMessage c = (ControlMessage) sInput.readObject();
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
                            for (ClientThread t : tracker.groups.get(name)) {
                                lmr.users.add(t.username);
                            }

                        }
                        sOutput.writeObject(lmr);
                        break;
                    case JoinGroup:
                        name = c.getInfo();
                        JoinGroupReply inf = new JoinGroupReply();
                        inf.users = new ArrayList<>();
                        if(tracker.groups.containsKey(name)){
                            for (ClientThread t : tracker.groups.get(name)) {
                                inf.users.add(new UserInfo(t.username,t.id,t.ip,t.port));
                            }
                        }
                        //should we send the new guy as part of the group
                        tracker.groups.put(name,this);
                        sOutput.writeObject(inf);
                        break;
                    case ExitGroup:
                        name = c.getInfo();
                        ControlReply ack = new ControlReply();
                        tracker.groups.remove(name,this);
                        sOutput.writeObject(ack);
                        break;
                    case Quit:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
