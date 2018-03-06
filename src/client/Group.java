import java.util.LinkedList;

public class Group {
    public String name;
    public LinkedList<Member> members;
    
    public Group(String name) {
        this.name = name;
        members = new LinkedList<Member>();
    }

    public void addMember(long id, String ip, int port) {
        Member m = new Member(id,ip, port);
        members.add(m);
    }
}