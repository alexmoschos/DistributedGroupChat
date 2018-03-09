package  client;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;


public class IsisMessageHandler extends MessageHandler{

    private long s = 0L;

    public IsisMessageHandler() {
        Comparator<Message> comparator = new MessageComparator();
        setComparator(comparator);
    }

    @Override
    public void sendMessage(String message) {

        long clientId = Client.getClientId();
        String groupId = Client.getCurrentGroupId();
        long messageId = getNextMessageId();

        Iterator<Member> recipient = null;
        int num_of_recipients = 0;

        // critical section
        // we need to make sure that GroupMembers won't
        // be dropped (due to info update, caused by a
        // heartbeat) before we can access them

        InformationController.getLock().lock();
        try {
            recipient = InformationController.getGroupMembers(Client.getCurrentGroupId());
            num_of_recipients = InformationController.getNumberOfMembersInGroup(Client.getCurrentGroupId());
        } finally {
            InformationController.getLock().unlock();
        }


        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        try {
            if (recipient == null) {
                // there is no group with such id
                return;
            }

            Message msg = new Message(clientId, groupId, messageId, -1,Client.getClientId(), false, num_of_recipients, message);

            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(msg);
            oo.close();

            byte[] serializedMessage = bStream.toByteArray();
            DatagramSocket socket = getSocket();

            while (recipient.hasNext()) {
                Member m = recipient.next();

                DatagramPacket packet = new DatagramPacket(serializedMessage,
                        serializedMessage.length, m.getIp(), m.getPort());
                socket.send(packet);
            }

        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }

    private void sendPacketToAll(String groupId, byte[] serializedMessage) {
        Iterator<Member> recipient = null;
        int num_of_recipients = 0;
        DatagramSocket socket = getSocket();

        recipient = InformationController.getGroupMembers(Client.getCurrentGroupId());
        num_of_recipients = InformationController.getNumberOfMembersInGroup(Client.getCurrentGroupId());

        while (recipient.hasNext()) {
            try {
                Member m = recipient.next();

                DatagramPacket packet = new DatagramPacket(serializedMessage,
                        serializedMessage.length, m.getIp(), m.getPort());
                socket.send(packet);
            } catch (IOException e) {
                // udp is not a trusted protocol. haha
                // do nothing
            }
        }

    }

    @Override
    public void receiveMessage(Message msg) {
        //critical section
        InformationController.getLock().lock();
        try {



            Group g = InformationController.getGroup(msg.getGroupId());
            if (g == null) {
                // group doesn't exists
                // ignore message
                return;
            }
            PriorityQueue<Message> messages = g.messages;


            if (msg.getUserId() == -1) {
                // proposal message

                msg.setUserId(Client.getClientId());
                for (Message init_message : messages) {
                    if (init_message.equals(msg)) {

                        init_message.setCounter(init_message.getCounter() - 1);

                        if (msg.getSuggestedPriority() > init_message.getSuggestedPriority()) {

                            init_message.setSuggestedPriority(msg.getSuggestedPriority());
                            init_message.setUserSuggest(msg.getUserSuggest());

                        }

                        messages.remove(init_message);
                        messages.add(init_message);

                        if (init_message.getCounter() == 0) {
                            // should inform the priority selected for this message.

                            long clientId = Client.getClientId();
                            String groupId = msg.getGroupId();
                            long messageId = msg.getMessageId();
                            long suggestedPriority = init_message.getSuggestedPriority();
                            long userSuggested = init_message.getUserSuggest();

                            Message reply = new Message(clientId, groupId, messageId, suggestedPriority, userSuggested, false, 1, null);

                            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                            ObjectOutput oo = new ObjectOutputStream(bStream);
                            oo.writeObject(reply);
                            oo.close();

                            byte[] serializedMessage = bStream.toByteArray();
                            sendPacketToAll(msg.getGroupId(), serializedMessage);

                        }
                        break;
                    }
                }

            }

            else if (msg.getSuggestedPriority() == -1) {
                // put message to queue
                messages.add(msg);



                long clientId = Client.getClientId();
                String groupId = msg.getGroupId();
                long messageId = msg.getMessageId();
                long suggestedPriority = ++s;

                Message reply = new Message(-1, groupId, messageId, suggestedPriority,Client.getClientId(), false, 1, null);

                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                ObjectOutput oo = new ObjectOutputStream(bStream);
                oo.writeObject(reply);
                oo.close();

                byte[] serializedMessage = bStream.toByteArray();
                DatagramSocket socket = getSocket();

                Member m = InformationController.getMember(msg.getUserId());

                DatagramPacket packet = new DatagramPacket(serializedMessage,
                        serializedMessage.length, m.getIp(), m.getPort());
                socket.send(packet);


            }

            else if (msg.getSuggestedPriority() >= 0) {
                for (Message init_message : messages) {
                    if (init_message.equals(msg)) {
                        messages.remove(msg);
                        s = Long.max(s, msg.getSuggestedPriority());
                        init_message.setSuggestedPriority(msg.getSuggestedPriority());
                        init_message.setUserSuggest(msg.getUserSuggest());
                        init_message.setStatus(true);
                        messages.add(init_message);
                        break;
                    }
                }

            }

            deliverMessage(msg.getGroupId());

        } catch (IOException e) {
            // FIXME
            System.out.println(e.toString());
            return;
        } finally {
            InformationController.getLock().unlock();
        }

    }



    @Override
    public void deliverMessage(String groupId) {
        Group g = InformationController.getGroup(groupId);
        if (g == null) {
            // group doesn't exists
            // ignore message
            return;
        }
        String groupName = g.name;
        PriorityQueue<Message> messages = g.messages;
        while (!messages.isEmpty() && messages.peek().getStatus()) {
            Member sender = InformationController.getMember(messages.peek().getUserId());
            if(Client.isDebugMode()){
                BufferedWriter out = null;
                try
                {
                    FileWriter fstream = new FileWriter(Client.getClientId()+"_"+groupName+".txt", true); //true tells to append data.
                    out = new BufferedWriter(fstream);
                    out.write("in " + groupName + " " + sender.getUsername() + " says:: ");
                    out.write(messages.peek().getMessage()+"\n");
                }
                catch (IOException e)
                {
                    System.err.println("Error: " + e.getMessage());
                }
                finally
                {
                    if(out != null) {
                        try{
                            out.close();
                        }
                        catch (IOException e){
                            System.err.println("Error: " + e.getMessage());
                        }

                    }
                }
            }
            else{
                System.out.print("in " + groupName + " " + sender.getUsername() + " says:: ");
                System.out.println(messages.peek().getMessage());
            }
            messages.remove();
        }
    }
}