package client;

import java.lang.Runnable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InformationController {
    private static Lock lock = new ReentrantLock();
    private static HashMap<String, Group> groups;
    private static HashMap<Long, Member> members;
    
    public InformationController() {
        groups = new HashMap<String, Group>();
        members = new HashMap<Long, Member>();
    }

    public static Lock getLock() {
        return lock;
    }

    public static String getGroupName(String groupId) {
        lock.lock();
        try {
            if (!groups.containsKey(groupId))
                return null;
            return groups.get(groupId).name;
        } finally {
            lock.unlock();
        }
    }
    public static void deleteGroup(String groupId){
        lock.lock();
        try{
            if(!groups.containsKey(groupId))
                return;
            groups.remove(groupId);
            return;
        }
        finally {
            lock.unlock();
        }
    }

    public static void addMember(Member m) {
        lock.lock();
        try {
            if (members.containsKey(m.getId()))
                return;

            members.put(m.getId(), m);
        } finally {
            lock.unlock();
        }
    }

    public static Group getGroup(String groupId) {
        lock.lock();
        try {
            return groups.get(groupId);
        } finally {
            lock.unlock();
        }
    }
    public static void addGroup(Group g) {
        lock.lock();
        try {
            if (!groups.containsKey(g.name))
                groups.put(g.name, g);
        } finally {
            lock.unlock();
        }
    }
    public static ArrayList<String> getAllGroups(){
        lock.lock();
        ArrayList<String> result = new ArrayList<String>();
        Iterator it = InformationController.groups.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            result.add((String)pair.getKey());
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
        }
        lock.unlock();
        return result;

    }
    public static Iterator<Member> getGroupMembers(String groupId) {
        lock.lock();
        try {
            if (!groups.containsKey(groupId))
                return null;
            return groups.get(groupId).members.iterator();
        } finally {
            lock.unlock();
        }
    }

    public static Member getMember(Long memberId) {
        lock.lock();
        try {
            return members.get(memberId);
        } finally {
            lock.unlock();
        }
    }

    public static PriorityQueue<Message> getGroupMessages(String groupId) {
        lock.lock();
        try {
            if (!groups.containsKey(groupId))
                return null;
            return groups.get(groupId).messages;
        } finally {
            lock.unlock();
        }
    }

    public static void addMessageToGroup(String groupId, Message m) {
        lock.lock();
        try {
            if (!groups.containsKey(groupId))
                return;
            groups.get(groupId).messages.add(m);
        } finally {
            lock.unlock();
        }
    }

    public static void setMessageTimer(String groupId, Message msg, MessageHandler mh) {
        lock.lock();
        try {
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
                    if (m.getExpectedMessageId() < msg.getMessageId()) {
                        m.setExpectedMessageId(msg.getMessageId());
                        mh.deliverMessage(msg.getGroupId());
                    }
                }
            }, 1000L);

            groups.get(groupId).timers.put(String.valueOf(msg.getUserId()) + "," + String.valueOf(msg.getMessageId()), t);
        } finally {
            lock.unlock();
        }
    }

    public static void cancelMessageTimer(String groupId, Message msg) {
        lock.lock();
        try {
            if (!groups.containsKey(groupId))
                return;

            // ignore if timer doesn't exist
            if (!groups.get(groupId).timers.containsKey(String.valueOf(msg.getUserId()) + "," + String.valueOf(msg.getMessageId())))
                return;

            Timer t = groups.get(groupId).timers.get(String.valueOf(msg.getUserId()) + "," + String.valueOf(msg.getMessageId()));
            t.cancel();
        } finally {
            lock.unlock();
        }
    }
    
}