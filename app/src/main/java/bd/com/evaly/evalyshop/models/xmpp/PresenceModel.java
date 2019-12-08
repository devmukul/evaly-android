package bd.com.evaly.evalyshop.models.xmpp;

import org.jivesoftware.smack.packet.Presence;

import java.io.Serializable;

public class PresenceModel implements Serializable {

    String user;
    String status;
    Presence.Mode mode;
    int userStatus;
    long time;

    public PresenceModel(){}

    public PresenceModel(String user, String status, Presence.Mode mode, int userStatus, long time){
        this.user = user;
        this.status = status;
        this.mode = mode;
        this.userStatus = userStatus;
        this.time = time;
//        Logger.d(user);
    }

    public long getTime() {
        return time;
    }

    public String getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }

    public Presence.Mode getMode() {
        return mode;
    }

    public int getUserStatus() {
        return userStatus;
    }

}
