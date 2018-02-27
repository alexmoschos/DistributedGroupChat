import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Tracker {
    private ServerSocket server;
    private Vector<ClientThread> peers;
    Group groups;
    private int nextId;
    public int getNextId() {
        return nextId++;
    }

    public Tracker(int PortNumber){
        nextId = 0;
        peers = new Vector<>();
        groups = new Group();
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
