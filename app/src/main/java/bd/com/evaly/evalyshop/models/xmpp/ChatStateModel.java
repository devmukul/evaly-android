package bd.com.evaly.evalyshop.models.xmpp;

import android.os.Parcel;
import android.os.Parcelable;

import org.jivesoftware.smackx.chatstates.ChatState;

import java.io.Serializable;

public class ChatStateModel implements Serializable {
    String user;
    ChatState chatState;

    public ChatStateModel(String user, ChatState chatState) {
        this.user = user;
        this.chatState = chatState;
    }

    public String getUser(){
        return this.user;
    }

    public ChatState getChatState(){
        return this.chatState;
    }


}
