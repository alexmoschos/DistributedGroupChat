package client;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Timer;

public class Group {
    public String name;
    public LinkedList<Member> members;
    public HashMap<String, Timer> timers;
    public PriorityQueue<Message> messages;
    
    public Group(String name) {
        this.name = name;
        members = new LinkedList<Member>();
        timers =new HashMap<>();
        messages = new PriorityQueue<>();
    }

    public void addMember(long id, InetAddress ip, int port, String username) {
        Member m = new Member(id,ip, port, username);
        members.add(m);
    }
}