package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.activation.UnknownObjectException;
import java.net.InetAddress;
import java.lang.Runnable;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Comparator;

abstract public class MessageHandler implements Runnable{
    private DatagramSocket socket = null;
    private long nextMessageId = 0L;
    private InetAddress localAddress = null;
    private byte[] buf = new byte[65500];
    private Comparator<Message> comparator = null;

    public MessageHandler() {
        while (localAddress == null) {
            try {
                localAddress = InetAddress.getLocalHost();
            }catch (UnknownHostException e) {
                System.out.println(e.toString());
                localAddress = null;
            }
        }

        while (socket == null) {
            try {
                socket = new DatagramSocket();
                System.out.println("listening on address: " + localAddress.getHostAddress());
                System.out.println("port: " + socket.getLocalPort());
            } catch (SocketException e) {
                System.out.println(e.toString());
                socket = null;
            }
            
        }
    }

    public long getNextMessageId() {
        return nextMessageId++;
    }

    public void setNextMessageId(long new_id) {
        nextMessageId = new_id;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

    public void setComparator(Comparator<Message> comparator) {
        this.comparator= comparator;
    }

    public Comparator<Message> getComparator() {
        return comparator;
    }

    public void run() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);

                ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
            
                try {
                    Message msg = (Message) iStream.readObject();
                    receiveMessage(msg);

                } catch (ClassNotFoundException cnfe) {
                    System.out.println(cnfe.toString());
                }
                
                iStream.close();
                
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }

    abstract public void sendMessage(String message);
    abstract public void receiveMessage(Message msg);
    abstract public void deliverMessage(String groupId);
}

