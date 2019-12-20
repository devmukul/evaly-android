package bd.com.evaly.evalyshop.models.notification;

import com.google.gson.annotations.SerializedName;

public class NotificationCount {

    @SerializedName("unread_notification_count")
    private int count;

    public NotificationCount(){

    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
