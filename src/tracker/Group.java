import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Group {
    ConcurrentHashMap<String,Vector<ClientThread>> groups;

    public Group() {
        this.groups = new ConcurrentHashMap<>();
    }
}
