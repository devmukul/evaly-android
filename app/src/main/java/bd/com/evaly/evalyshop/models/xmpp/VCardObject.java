package bd.com.evaly.evalyshop.models.xmpp;

import org.jxmpp.jid.Jid;

import java.io.Serializable;

public class VCardObject implements Serializable {
    private String name;
    private Jid jid;
    private String url;
    private int status;

    public VCardObject(String name, Jid jid, String url, int status) {
        this.name = name;
        this.jid = jid;
        this.url = url;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Jid getJid() {
        return jid;
    }

    public void setJid(Jid jid) {
        this.jid = jid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
