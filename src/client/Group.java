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

    public void addMember(Member m) {
        members.add(m);
    }

    public void dropMembers() {
        members = new LinkedList<Member>();
    }
}