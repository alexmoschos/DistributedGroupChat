package client;

import java.io.Serializable;
import java.lang.Comparable;
import java.util.Objects;

public class Message implements Serializable, Comparable<Message> {
    protected static final long serialVersionUID = 10881L;
    private long userId;
    private String groupId;
    private long messageId;
    private long suggestedPriority;
    private long userSuggest;
    private boolean status;
    private String message;
    private long counter;
    public Long receiveTime;

    public Message(long userId, String groupId, long messageId, long suggestedPriority, long userSuggest, boolean status, long counter, String message) {
        this.userId = userId;
        this.groupId = groupId;
        this.messageId = messageId;
        this.suggestedPriority = suggestedPriority;
        this.userSuggest = userSuggest;
        this.status = status;
        this.counter = counter;
        this.message = message;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Message{" +
                "userId=" + userId +
                ", groupId='" + groupId + '\'' +
                ", messageId=" + messageId +
                ", suggestedPriority=" + suggestedPriority +
                ", userSuggest=" + userSuggest +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", counter=" + counter +
                '}';
    }

    public long getCounter() {

        return counter;
    }

    public void setCounter(long counter) {

        this.counter = counter;
    }

    public void setSuggestedPriority(long suggestedPriority) {

        this.suggestedPriority = suggestedPriority;
    }

    public void setUserSuggest(long userSuggest) {
        this.userSuggest = userSuggest;
    }

    public void setUserId(long userId) {

        this.userId = userId;
    }

    public long getUserId(){
        return userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public long getSuggestedPriority() {
        return suggestedPriority;
    }

    public long getUserSuggest() {
        return userSuggest;
    }

    public boolean getStatus() {
        return status;
    }

    public int compareTo(Message o) {
        if (this.messageId < o.getMessageId())
            return -1;
        if (this.messageId > o.getMessageId())
            return 1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return userId == message.userId &&
                messageId == message.messageId;
    }

}