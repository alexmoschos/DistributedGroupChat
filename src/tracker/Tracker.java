package tracker;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import common.UserInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Tracker {
    private ServerSocket server;
    //ConcurrentHashMap<ClientInfo,ClientThread> aliveClients;
    ArrayList<UserInfo> clients;
    Multimap<String,UserInfo> groups;
    private final AtomicInteger nextId = new AtomicInteger();
    public int getNextId() {
        return nextId.getAndIncrement();
    }

    public Tracker(int PortNumber){
        nextId.set(0);
        groups = Multimaps.synchronizedMultimap(ArrayListMultimap.create());
        clients = new ArrayList<>();
        try {
            server = new ServerSocket(PortNumber);
        }
        catch (IOException e) {
            //System.out.println(e);
            e.printStackTrace();
        }
    }
    public void listen(){
        try {
            Socket clientSocket = null;
            while (true) {
                System.out.println("Im listening");
                clientSocket = server.accept();
                ClientThread client = new ClientThread(clientSocket,this);
                client.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        System.out.println("Hello world");
        Tracker t = new Tracker(3000);
        t.listen();
    }
}
