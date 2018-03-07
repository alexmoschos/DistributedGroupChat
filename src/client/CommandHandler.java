package client;

import common.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class CommandHandler {

    public CommandHandler() {}

    public static void main(String[] args) throws Throwable {
        int port = 3000;
        String serverAddress = "localhost";
        Socket sock = new Socket(serverAddress, port);
        ObjectOutputStream sOutput = new ObjectOutputStream(sock.getOutputStream());
        ObjectInputStream sInput = new ObjectInputStream(sock.getInputStream());
        Scanner s = new Scanner(System.in);
        while (true) {
            String str = s.nextLine();
            if ("register".equals(str)) {
                sOutput.writeObject(new ControlMessage(ControlMessage.Type.Register, "192.168.1.1,80,alexm"));
                RegisterReply r = (RegisterReply) sInput.readObject();
                System.out.println(r.id);
                sOutput.writeObject(new ControlMessage(ControlMessage.Type.JoinGroup,"distrib"));
                JoinGroupReply zz = (JoinGroupReply) sInput.readObject();
                System.out.println(zz.users);
                sOutput.writeObject(new ControlMessage(ControlMessage.Type.ListGroups, ""));
                ListGroupsReply x = (ListGroupsReply) sInput.readObject();
                System.out.println(x.groups);
//                sOutput.writeObject(new ControlMessage(ControlMessage.Type.ListMembers,"distrib"));
//                ListMembersReply z;
//                z = (ListMembersReply) sInput.readObject();
//                System.out.println(z.users);

            } else {
                System.out.println("Hello world");
            }
        }
    }
    public void execute(String command){
        switch (command) {
            case "r":  // register user to tracker.
                Client.setClientId(registerClient());
                break;
            
            default: 
                System.out.println("Unkown command. Type !h for the help menu");
        }
    }

    public long registerClient() {
        return  1L;
    }

}

