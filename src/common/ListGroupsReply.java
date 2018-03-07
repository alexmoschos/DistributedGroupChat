package common;

import common.ControlReply;

import java.io.Serializable;
import java.util.ArrayList;

public class ListGroupsReply extends ControlReply implements Serializable {
    public ArrayList<String> groups;
}
