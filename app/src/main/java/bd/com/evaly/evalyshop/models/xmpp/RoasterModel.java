package bd.com.evaly.evalyshop.models.xmpp;

import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.packet.Presence;
import org.jxmpp.jid.Jid;

import java.io.Serializable;


public class RoasterModel implements Serializable, Comparable<RoasterModel> {

    public Jid roasterEntryUser;
    public Jid roasterPresenceFrom;
    public String presenceStatus;
    public Presence.Mode presenceMode;
    public int status;
    long time;
    private String lastMessageId;
    private String name;

    public RoasterModel(Jid roasterEntryUser, Jid roasterPresenceFrom, String presenceStatus, Presence.Mode presenceMode, int status, String name){
        this.roasterEntryUser = roasterEntryUser;
        this.roasterPresenceFrom = roasterPresenceFrom;
        this.presenceStatus = presenceStatus;
        this.presenceMode = presenceMode;
        this.status = status;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public Jid getRoasterEntryUser() {
        return roasterEntryUser;
    }

    public void setRoasterEntryUser(Jid roasterEntryUser) {
        this.roasterEntryUser = roasterEntryUser;
    }

    public Jid getRoasterPresenceFrom() {
        return roasterPresenceFrom;
    }

    public void setRoasterPresenceFrom(Jid roasterPresenceFrom) {
        this.roasterPresenceFrom = roasterPresenceFrom;
    }

    public String getPresenceStatus() {
        return presenceStatus;
    }

    public void setPresenceStatus(String presenceStatus) {
        this.presenceStatus = presenceStatus;
    }

    public Presence.Mode getPresenceMode() {
        return presenceMode;
    }

    public void setPresenceMode(Presence.Mode presenceMode) {
        this.presenceMode = presenceMode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int compareTo(RoasterModel roasterModel) {
//        Logger.d("_+_+_+_+_+_+");
        return Long.compare(getTime(), roasterModel.getTime());

    }
}