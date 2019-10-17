package bd.com.evaly.evalyshop.models.xmpp;

import android.os.Parcel;
import android.os.Parcelable;

import bd.com.evaly.evalyshop.util.Utils;

public class ChatItem implements Parcelable {

    private String chat;
    private String name;
    private String image;
    private String nick_name;
    private long time;
    private String sender;
    private String receiver;
    private String uid;
    private String messageType;
    private boolean isMine;
    private String messageId;
    private boolean isUnread;
    private String large_image;


    public ChatItem(String chat, String name, String image, String nick_name, long time, String sender, String receiver, String messageType, boolean isMine, String large_image) {
        this.chat = chat;
        this.name = name;
        this.image = image;
        this.nick_name = nick_name;
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
        this.isMine = isMine;
        this.messageType = messageType;
        this.large_image = large_image;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getLarge_image() {
        return large_image;
    }

    public void setLarge_image(String large_image) {
        this.large_image = large_image;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public long getLognTime(){
        return time;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setIsMine(Boolean isMine){
        this.isMine = isMine;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public String getChat(){
        return this.chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getTime(){
        return Utils.getTimeAgo(time);
    }

    public String getSender(){
        return this.sender;
    }
    public String getReceiver(){
        return this.receiver;
    }

    public boolean isMine(){
        return this.isMine;
    }

    //Parcellable Content
    protected ChatItem(Parcel in) {
        chat = in.readString();
        time = in.readLong();
        name = in.readString();
        sender = in.readString();
        receiver = in.readString();
        nick_name = in.readString();
        image = in.readString();
        isMine = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chat);
        dest.writeString(name);
        dest.writeString(nick_name);
        dest.writeString(image);
        dest.writeString(sender);
        dest.writeString(receiver);
        dest.writeLong(time);
        dest.writeByte((byte) (isMine ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Creator<ChatItem> CREATOR = new Creator<ChatItem>() {
        @Override
        public ChatItem createFromParcel(Parcel in) {
            return new ChatItem(in);
        }

        @Override
        public ChatItem[] newArray(int size) {
            return new ChatItem[size];
        }
    };
}
