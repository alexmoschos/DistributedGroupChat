package common;

import java.io.Serializable;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return id == userInfo.id &&
                port == userInfo.port &&
                Objects.equals(username, userInfo.username) &&
                Objects.equals(ip, userInfo.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, id, ip, port);
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
