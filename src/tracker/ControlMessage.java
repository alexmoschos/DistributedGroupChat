import java.io.Serializable;

public class ControlMessage implements Serializable {
    protected static final long serialVersionUID = 1112122200L;
    enum Type {
        Register,
        ListGroups,
        ListMembers,
        JoinGroup,
        ExitGroup,
        Quit
    }
    private Type type;
    private String info;

    public ControlMessage(Type type, String info) {
        this.type = type;
        this.info = info;
    }
    Type getType(){
        return type;
    }

    public String getInfo() {
        return info;
    }

}
