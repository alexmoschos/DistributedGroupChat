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
    private static long clientId = -1L;
    private static String currentGroupId = "distrib";

    public static void main(String[] args) throws IOException, ClassNotFoundException, Throwable {

        /* main loop to read input from the user */
        BufferedReader br = new BufferedReader(new InputStreamReader (System.in));
        MessageHandler mh = new IsisMessageHandler();
        CommandHandler ch = new CommandHandler(mh);
        InformationController ic = new InformationController();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Socket sock;
                ObjectOutputStream sOutput;
                ObjectInputStream sInput;
                int port = 3000;
                String serverAddress = "localhost";
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
        new Thread(mh).start();

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
                System.out.print("[alexm]>");
            } catch(IOException e) {
                System.out.println(e.toString());
            }
            catch (StringIndexOutOfBoundsException e){
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
}
