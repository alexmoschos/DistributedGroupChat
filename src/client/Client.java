package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Thread;

public class Client {
    private static long clientId = -1L;
    private static long currentGroupId = -1L;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
        /* main loop to read input from the user */
        BufferedReader br = new BufferedReader(new InputStreamReader (System.in));
        CommandHandler ch = new CommandHandler();
        MessageHandler mh = new MessageHandler(MessageHandler.ProtocolType.FIFO);
        InformationController ic = new InformationController();

        // start ic
        new Thread(ic).start();

        while (true) {
            try {
                String line = br.readLine();
                if (line.charAt(0) == '!') {
                    String command = line.substring(1);
                    if (clientId == -1 && !command.equals("r")) {
                        clientNotRegistered();
                        continue;
                    }
                    ch.execute(command);
                }
                else {
                    if (clientId == -1) {
                        clientNotRegistered();
                        continue;
                    }
                    mh.sendMessage(line);
                }
            } catch(IOException e) {
                System.out.println(e.toString());
            }
        }
        
        // int port = 3000;
        // String serverAddress = "localhost";
        // Socket sock = new Socket(serverAddress,port);
        // ObjectOutputStream sOutput = new ObjectOutputStream(sock.getOutputStream());
        // ObjectInputStream sInput = new ObjectInputStream(sock.getInputStream());
        // Scanner s = new Scanner(System.in);
        // while(true) {
        //     String str = s.nextLine();
        //     if("register".equals(str)){
        //         sOutput.writeObject(new ControlMessage(ControlMessage.Type.Register,"192.168.1.1,80,alexm"));
        //         sOutput.writeObject(new ControlMessage(ControlMessage.Type.ListGroups,""));
        //         System.out.println((String)sInput.readObject());
        //     } else {
        //         System.out.println("Hello world");
        //     }
        // }
    }

    public static void setClientId(long id) {
        clientId = id;
    }

    public static long getClientId() {
        return clientId;
    }

    public static Long getCurrentGroupId() {
        return new Long(currentGroupId);
    }

    public static void setCurrentGroupId(long newId) {
        currentGroupId = newId;
    }

    private static void clientNotRegistered() {
        System.out.println("You are not registered yet!");
        System.out.println("In order to continue with the chat you need to register with the tracker first.");
        System.out.println("To register type !r");
    }
}
