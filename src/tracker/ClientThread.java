package tracker;

import common.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private void scheduleTimer(int id) throws IllegalStateException {
        //System.out.println("I entered here");
        Timer x = new Timer();

        x.schedule(new TimerTask() {
            @Override
            public void run() {

                System.out.println("A client has died " + id);
                synchronized (tracker.groups) {
                    for (String key : tracker.groups.keySet()) {
                        tracker.groups.remove(key, new UserInfo(username, id, ip, port));
                    }
                }
            }
        },10*1000);

        tracker.heartbeat.set(id,x);
    }
    public void run() {
        // System.out.println("Client thread started");
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
                synchronized(tracker.clients) {
                    tracker.clients.add(id, client);
                }
                synchronized(tracker.timerLocks) {
                    tracker.timerLocks.add(id, new ReentrantLock());
                }
                tracker.timerLocks.get(id).lock();
                tracker.heartbeat.add(id,new Timer());
                scheduleTimer(id);
                tracker.timerLocks.get(id).unlock();

            } else if (c.getId() == -1){

            } else {
                id = c.getId();
//                System.out.println(id);
//                System.out.println(tracker.clients);
                tracker.timerLocks.get(id).lock();
                //System.out.println("Timer cancelled" + id);
                tracker.heartbeat.get(id).cancel();
                scheduleTimer(id);
                tracker.timerLocks.get(id).unlock();
                UserInfo client = null;
                synchronized (tracker.clients) {
                    client = tracker.clients.get(id);
                }
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
                        lmr.users = new ArrayList<>(tracker.groups.get(name));
                    }
                    sOutput.writeObject(lmr);
                    break;
                case JoinGroup:
                    name = c.getInfo();
                    JoinGroupReply inf = new JoinGroupReply();
                    inf.users = new ArrayList<>();
                    synchronized (tracker.groups) {
                        if (tracker.groups.containsKey(name)) {
                            if (!tracker.groups.get(name).contains(new UserInfo(username, id, ip, port))) {
                                tracker.groups.put(name, new UserInfo(username, id, ip, port));
                            }
                        } else {
                            tracker.groups.put(name, new UserInfo(username, id, ip, port));
                        }
                    }
                    if(tracker.groups.containsKey(name)){
                        inf.users = new ArrayList<>(tracker.groups.get(name));
                    }
                    //should we send the new guy as part of the group
                    sOutput.writeObject(inf);
                    break;
                case ExitGroup:
                    name = c.getInfo();
                    ControlReply ack = new ControlReply();
                    synchronized (tracker.groups) {
                        tracker.groups.remove(name, new UserInfo(username, id, ip, port));
                    }
                    sOutput.writeObject(ack);
                    break;
                case Quit:
                    synchronized (tracker.groups) {
                        for (String key : tracker.groups.keySet()) {
                            tracker.groups.remove(key, new UserInfo(username, id, ip, port));
                        }
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
