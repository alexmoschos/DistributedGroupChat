package common;

import java.io.Serializable;

public class UserInfo implements Serializable {
    public String username;
    public int id;
    public String ip;
    public int port;

    public UserInfo(String username, int id, String ip, int port) {
        this.username = username;
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                ", id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
