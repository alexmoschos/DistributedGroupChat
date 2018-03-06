public class Member {
    private long id;
    private String ip;
    private int port;

    public Member(long id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port  = port;
    }

    public long getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }
    
    public int getPort() {
        return port;
    }
}