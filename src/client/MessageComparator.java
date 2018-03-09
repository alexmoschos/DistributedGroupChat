package client;


import java.util.Comparator;

public class MessageComparator implements Comparator<Message> {
    @Override
    public int compare(Message o1, Message o2) {
        if (o1.getSuggestedPriority() < o2.getSuggestedPriority())
            return -1;
        if (o1.getSuggestedPriority() > o2.getSuggestedPriority())
            return 1;
        if (o1.getStatus() == false && o2.getStatus() == true)
            return -1;
        if (o1.getStatus() == true && o2.getStatus() == false)
            return 1;

        if (o1.getUserSuggest() < o2.getUserSuggest())
            return -1;

        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
