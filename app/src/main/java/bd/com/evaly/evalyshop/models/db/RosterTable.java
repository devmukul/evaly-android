package bd.com.evaly.evalyshop.models.db;


import java.io.Serializable;

public class RosterTable implements Serializable {
    public String id;
    public String rosterName;
    public String lastMessage;
    public int status;
    public int unreadCount;
    public long time;
    public String name;
    public String nick_name;
    public String imageUrl;
    public String messageId;
    public boolean isSelected;

}
