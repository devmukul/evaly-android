package bd.com.evaly.evalyshop.models.xmpp;

import org.jxmpp.jid.Jid;

public class MessageBody {
    private String name;
    private String body;
    private String image;
    private String nick_name;
    private long time;
    private Jid from;

    public MessageBody(String name, String body, String image, String nick_name, Jid from) {
        this.name = name;
        this.body = body;
        this.image = image;
        this.nick_name = nick_name;
        this.from = from;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public Jid getFrom() {
        return from;
    }

    public void setFrom(Jid from) {
        this.from = from;
    }
}
