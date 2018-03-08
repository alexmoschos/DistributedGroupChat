package common;

import java.io.Serializable;
import java.util.ArrayList;

public class ListMembersReply extends ControlReply implements Serializable{
    public ArrayList<UserInfo> users;
}
