
public class MessageHandler {

    public enum ProtocolType {
        FIFO,
        TOTAL_ORDER
    }
    
    private ProtocolType protocol;

    public MessageHandler(ProtocolType t) {
        protocol = t;
    }

    public void sendMessage(String message) {
        if (protocol == ProtocolType.FIFO) {
            System.out.println("fifo");
        }   
        else {
            System.out.println("TotalOrder");
        }
    }

    

}

