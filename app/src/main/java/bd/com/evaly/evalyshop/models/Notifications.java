package bd.com.evaly.evalyshop.models;

public class Notifications {

    private String id,imageURL,message,time, content_type, content_url;
    private boolean read;

    public Notifications(String id, String imageURL, String message, String time, String content_type, String content_url, boolean read) {
        this.id = id;
        this.imageURL = imageURL;
        this.message = message;
        this.time = time;
        this.content_type = content_type;
        this.content_url = content_url;
        this.read = read;
    }

    public Notifications(){
        
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
