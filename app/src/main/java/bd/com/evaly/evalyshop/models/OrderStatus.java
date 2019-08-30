package bd.com.evaly.evalyshop.models;

public class OrderStatus {

    String time,status,note;

    public OrderStatus(String time, String status, String note) {
        this.time = time;
        this.status = status;
        this.note = note;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
