package tracker;

import common.ControlMessage;
import common.ControlReply;
import common.ListGroupsReply;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
                    break;
                }
            }
            System.out.println("My new id is " + id.toString());
            // we only enter here when the other guy is registered
            ControlReply reply;
            String name = "";
            while(true) {
                ControlMessage c = (ControlMessage) sInput.readObject();
                switch(c.getType()){
                    case Register:
                        System.out.println("Something wrong happened this client registered twice" + id.toString());
                        break;
                    case ListGroups:
                        ListGroupsReply r = new ListGroupsReply();
                        for(String n : tracker.groups.keySet()){
                            r.groups.add(n);
                        }
                        sOutput.writeObject(r);
                        break;
                    case ListMembers:
                        name = c.getInfo();
//                        if(tracker.groups.contains(name)) {
//                            reply += name + ":";
//                            for (ClientThread t : tracker.groups.get(name)) {
//                                reply += t.username + ",";
//                            }
//                            reply += "\n";
//                        } else {
//                            reply = "Group doesnt exist\n";
//                        }
                        break;
                    case JoinGroup:
                        name = c.getInfo();
//                        if(tracker.groups.contains(name)){
//                            Vector<ClientThread> v = tracker.groups.get(name);
//                        } else {
//
//                        }
                        break;
                    case ExitGroup:
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
