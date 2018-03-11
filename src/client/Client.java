package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;

import client.FifoMessageHandler;
import common.ControlMessage;
import common.JoinGroupReply;
import common.ListMembersReply;
import common.UserInfo;

import javax.swing.text.html.HTMLDocument;
import java.lang.Thread;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
    public static Long startTime = null;
    public static Long endTime = null;
    public static int count = 0;
    private static long clientId = -1L;
    private static String currentGroupId = "distrib";
    private static String username;
    private static boolean debugMode = false;

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static int getTrackerPort() {
        return trackerPort;
    }

    public static String getTrackerAddr() {
        return trackerAddr;
    }

    public static String getProtocol() {
        return protocol;
    }

    private static int trackerPort = 3000;
    private static String trackerAddr = "localhost";
    private static String protocol = "fifo";


    public static void main(String[] args) throws IOException, ClassNotFoundException, Throwable {

        /* main loop to read input from the user */
        BufferedReader br = new BufferedReader(new InputStreamReader (System.in));


        InformationController ic = new InformationController();
        Timer timer = new Timer();
        for (String arg : args) {
            String[] parts = arg.split("=");
            switch (parts[0]){
                case "debug":
                    debugMode = Boolean.valueOf(parts[1]);
                    break;
                case "tracker":
                    String[] addr = parts[1].split(":");
                    trackerAddr = addr[0];
                    trackerPort = Integer.parseInt(addr[1]);
                    break;
                case "protocol":
                    protocol = parts[1];
                    break;
                default:
                    System.out.println("Invalid argument");
            }
        }
//        System.out.println(debugMode);
//        System.out.println(trackerAddr);
//        System.out.println(trackerPort);
//        System.out.println(protocol);
        MessageHandler mh ;
        if(protocol.equals("fifo")) mh = new FifoMessageHandler();
        else mh = new IsisMessageHandler();
        CommandHandler ch = new CommandHandler(mh);
        new Thread(mh).start();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Socket sock = null;
                ObjectOutputStream sOutput;
                ObjectInputStream sInput;
                int port = trackerPort;
                String serverAddress = trackerAddr;
                try {
                    for (String group : InformationController.getAllGroups()) {
                        sock = new Socket(serverAddress, port);
                        sOutput = new ObjectOutputStream(sock.getOutputStream());
                        sInput = new ObjectInputStream(sock.getInputStream());
                        sOutput.writeObject(new ControlMessage(ControlMessage.Type.ListMembers, group, (int) Client.getClientId()));

                        ListMembersReply z;
                        z = (ListMembersReply) sInput.readObject();
                        sock.close();
                        InformationController.getLock().lock();
                        try {
                            Group g = InformationController.getGroup(group);
                            if (g == null)
                                g = new Group(group,mh);
                            g.dropMembers();
                            for (UserInfo user : z.users) {
                                Member m = new Member(user.id, InetAddress.getByName(user.ip), user.port, user.username);
                                g.addMember(m);
                                InformationController.addMember(m);
                            }

                            // remove dead message from isis_messages queue
                            for (Message msg : g.isis_messages) {
                                if (!msg.getStatus() && !z.users.contains(msg.getUserId())) {
                                    // this is a dead message remove it from queue
                                    g.isis_messages.remove(msg);
                                }
                            }
                            InformationController.addGroup(g);
                        } finally {
                            InformationController.getLock().unlock();
                        }
                        //System.out.println(z.users);
                    }
                }
                catch (IOException e) {
                        // System.out.println(e);
                }
                catch (ClassNotFoundException e){
                    System.out.println(e);
                }





            }
        },2*1000, 2*1000);

        // start message handler


        while (true) {
            String line = null;
            try {
                line = br.readLine();
                if (line.equals("")) continue;
                if (line.charAt(0) == '!') {
                    String command = line.substring(1);
                    if (clientId == -1 && !command.equals("r")) {
                        clientNotRegistered();
                        continue;
                    }
                    ch.execute(command);
                } else {
                    if (clientId == -1) {
                        clientNotRegistered();
                        continue;
                    }
                    mh.sendMessage(line);
                }
                System.out.print("[" + Client.username + "]>");
            } catch (IOException e) {
                System.out.println(e.toString());
            } catch (StringIndexOutOfBoundsException e) {
                //                System.out.println("Exception in client: " + line);
                //                e.printStackTrace();
                System.out.println(e);
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

    public static String getCurrentGroupId() {
        return currentGroupId;
    }

    public static void setCurrentGroupId(String newId) {
        currentGroupId = newId;
    }

    private static void clientNotRegistered() {
        System.out.println("You are not registered yet!");
        System.out.println("In order to continue with the chat you need to register with the tracker first.");
        System.out.println("To register type !r");
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Client.username = username;
    }
}
