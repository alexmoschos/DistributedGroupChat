package common;

import java.io.Serializable;

public class ControlMessage implements Serializable {
    protected static final long serialVersionUID = 1112122200L;
    public enum Type {
        Register,
        ListGroups,
        ListMembers,
        JoinGroup,
        ExitGroup,
        Quit
    }
    private Type type;
    private String info;
    private int id;

    public ControlMessage(Type type, String info) {
        System.out.println("DEPRECATED: Stop using this ControlMessage.java constructor");
        this.type = type;
        this.info = info;
    }

    public ControlMessage(Type type, String info, int id) {
        this.type = type;
        this.info = info;
        this.id = id;
    }

    public Type getType(){
        return type;
    }

    public String getInfo() {
        return info;
    }

    public int getId() {
        return id;
    }
}
