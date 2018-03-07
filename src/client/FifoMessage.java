package client;

import java.io.Serializable;

public class FifoMessage implements Serializable {
    protected static final long serialVersionUID = 10881L;
    private long userId;
    private long groupId;
    private long messageId;
    private String message;

    public FifoMessage(long userId, long groupId, long messageId, String message) {
        this.userId = userId;
        this.groupId = groupId;
        this.messageId = messageId;
        this.message = message;
    }

    public long getUserId(){
        return userId;
    }

    public long getGroupId() {
        return groupId;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }
}