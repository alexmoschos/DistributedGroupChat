import java.lang.Runnable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;

public class InformationController implements Runnable {
    private ServerSocket server;
    private Socket clientSocket;
    public static final int PortNumber = 3001;

    public static HashMap<Long, Group> groups;
    public static HashMap<Long, Member> members;
    
    public InformationController() {
        groups = new HashMap<Long, Group>();
        members = new HashMap<Long, Member>();
    }

    public static String getGroupName(Long groupId) {
        return groups.get(groupId).name;
    }

    public static Iterator<Member> getGroupMembers(Long groupId) {
        return groups.get(groupId).members.iterator();
    }

    public static Member getMember(Long memberId) {
        return members.get(memberId);
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