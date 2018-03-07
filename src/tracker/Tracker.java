package tracker;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Tracker {
    private ServerSocket server;
    private Vector<ClientThread> peers;
    Multimap<String,ClientThread> groups;
    private final AtomicInteger nextId = new AtomicInteger();
    public int getNextId() {
        return nextId.getAndIncrement();
    }

    public Tracker(int PortNumber){
        nextId.set(0);
        peers = new Vector<>();
        groups = Multimaps.synchronizedMultimap(ArrayListMultimap.create());
        try {
            server = new ServerSocket(PortNumber);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
    public void listen(){
        try {
            Socket clientSocket = null;
            while (true) {
                System.out.println("Im listening");
                clientSocket = server.accept();
                ClientThread client = new ClientThread(clientSocket,this);
                peers.add(client);
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
