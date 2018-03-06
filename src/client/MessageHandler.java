import java.util.Iterator;

public class MessageHandler {

    public enum ProtocolType {
        FIFO,
        TOTAL_ORDER
    }
    
    private ProtocolType protocol;
    private long nextMessageId = 0L;

    public MessageHandler(ProtocolType t) {
        protocol = t;
    }

    public void sendMessage(String message) {
        if (protocol == ProtocolType.FIFO) {
            long clientId = Client.getClientId();
            long groupId = Client.getCurrentGroupId();
            long messageId = nextMessageId++;

            FifoMessage msg = new FifoMessage(clientId, groupId, messageId, message);

            Iterator<Member> recipient = 
                InformationController.getGroupMembers(Client.getCurrentGroupId());

            while (recipient.hasNext()) {
                Member m = recipient.next();
                // send message to that recipient
            }
        }   
        else {
            System.out.println("TotalOrder");
        }
    }

    

}

