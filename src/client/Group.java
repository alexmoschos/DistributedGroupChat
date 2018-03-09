package client;

import java.net.InetAddress;
import java.util.*;

public class Group {
    public String name;
    public LinkedList<Member> members;
    public HashMap<String, Timer> timers;
    public PriorityQueue<Message> messages;
    public Queue<Message> isis_messages;
    
    public Group(String name, MessageHandler mh) {
        this.name = name;
        members = new LinkedList<Member>();
        timers =new HashMap<>();
        messages = new PriorityQueue<>(11, mh.getComparator());
        isis_messages = new LinkedList<>();
    }

    public void addMember(Member m) {
        members.add(m);
    }

    public void dropMembers() {
        members = new LinkedList<Member>();
    }
}