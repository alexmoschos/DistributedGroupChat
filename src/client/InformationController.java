package client;

import java.lang.Runnable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

public class InformationController implements Runnable {
    private ServerSocket server;
    private Socket clientSocket;
    private static final int PortNumber = 3001;

    private static HashMap<Long, Group> groups;
    private static HashMap<Long, Member> members;
    
    public InformationController() {
        groups = new HashMap<Long, Group>();
        members = new HashMap<Long, Member>();
    }

    public static String getGroupName(Long groupId) {
        if (!groups.containsKey(groupId))
            return null;
        return groups.get(groupId).name;
    }

    public static Iterator<Member> getGroupMembers(Long groupId) {
        if (!groups.containsKey(groupId))
            return null;
        return groups.get(groupId).members.iterator();
    }

    public static Member getMember(Long memberId) {
        return members.get(memberId);
    }

    public static PriorityQueue<Message> getGroupMessages(Long groupId) {
        if (!groups.containsKey(groupId)) 
            return null;
        return groups.get(groupId).messages;
    }

    public static void addMessageToGroup(Long groupId, Message m) {
        if (!groups.containsKey(groupId))
            return;
        groups.get(groupId).messages.add(m);
    }

    public static void setMessageTimer(Long groupId, Message msg, MessageHandler mh) {
        if (!groups.containsKey(groupId))
            return;
        // if timer already exists return
        if (groups.get(groupId).timers.containsKey(String.valueOf(msg.getUserId()) + "," + String.valueOf(msg.getMessageId())))
            return;

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                Member m = InformationController.getMember(msg.getUserId());
                mh.deliverMessage(groupId);
                if (m.getExpectedMessageId() < msg.getMessageId())
                    m.setExpectedMessageId(msg.getMessageId());
            }
        },1000L);

        groups.get(groupId).timers.put(String.valueOf(msg.getUserId()) + "," + String.valueOf(msg.getMessageId()), t);
    }

    public static void cancelMessageTimer(Long groupId, Message msg) {
        if (!groups.containsKey(groupId))
            return;
        
        // ignore if timer doesn't exist
        if (!groups.get(groupId).timers.containsKey(String.valueOf(msg.getUserId()) + "," + String.valueOf(msg.getMessageId())))
            return;
        
        Timer t = groups.get(groupId).timers.get(String.valueOf(msg.getUserId()) + "," + String.valueOf(msg.getMessageId()));
        t.cancel();
    }


    public void run() {
        try {
            server = new ServerSocket(PortNumber);
            clientSocket = null;
            while (true) {
                clientSocket = server.accept();
                ObjectInputStream sInput  = new ObjectInputStream(clientSocket.getInputStream());
                
                try {
                    String incomingMessage = (String) sInput.readObject();
                    // parse the incomingMessage and update any necessary structures.
                } catch (ClassNotFoundException e) {
                    System.out.println(e.toString());
                }
                
                clientSocket.close();
                
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}