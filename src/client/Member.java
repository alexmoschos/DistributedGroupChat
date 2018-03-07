package client;

import java.net.InetAddress;

public class Member {
    private long id;
    private InetAddress ip;
    private int port;
    private long expectedMessageId = 0L;
    private String username;

    public Member(long id, InetAddress ip, int port, String username) {
        this.id = id;
        this.ip = ip;
        this.port  = port;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public InetAddress getIp() {
        return ip;
    }
    
    public int getPort() {
        return port;
    }

    public long getExpectedMessageId() {
        return expectedMessageId;
    }
    
    public void setExpectedMessageId(long newId) {
        expectedMessageId = newId;
    }

    public String getUsername() {
        return username;
    }
}