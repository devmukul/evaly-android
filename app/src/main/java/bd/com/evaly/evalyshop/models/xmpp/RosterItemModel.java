package bd.com.evaly.evalyshop.models.xmpp;

public class RosterItemModel {
    private String jid;
    private String last_message;
    private String last_unread_message_id;
    private String vcard_username;
    private String vcard;
    private int unseen_messages;

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getLast_unread_message_id() {
        return last_unread_message_id;
    }

    public void setLast_unread_message_id(String last_unread_message_id) {
        this.last_unread_message_id = last_unread_message_id;
    }

    public String getVcard_username() {
        return vcard_username;
    }

    public void setVcard_username(String vcard_username) {
        this.vcard_username = vcard_username;
    }

    public String getVcard() {
        return vcard;
    }

    public void setVcard(String vcard) {
        this.vcard = vcard;
    }

    public int getUnseen_messages() {
        return unseen_messages;
    }

    public void setUnseen_messages(int unseen_messages) {
        this.unseen_messages = unseen_messages;
    }
}
