import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int port = 3000;
        String serverAddress = "localhost";
        Socket sock = new Socket(serverAddress,port);
        ObjectOutputStream sOutput = new ObjectOutputStream(sock.getOutputStream());
        ObjectInputStream sInput = new ObjectInputStream(sock.getInputStream());
        Scanner s = new Scanner(System.in);
        while(true) {
            String str = s.nextLine();
            if("register".equals(str)){
                sOutput.writeObject(new ControlMessage(ControlMessage.Type.Register,"192.168.1.1,80,alexm"));
                sOutput.writeObject(new ControlMessage(ControlMessage.Type.ListGroups,""));
                System.out.println((String)sInput.readObject());
            } else {
                System.out.println("Hello world");
            }
        }
    }
}
