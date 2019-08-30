package bd.com.evaly.evalyshop.models;

public class TransactionItem {

    private int amount;
    private String date_time,event;


    public TransactionItem(){

    }

    public TransactionItem(int amount, String date_time, String event) {
        this.amount = amount;
        this.date_time = date_time;
        this.event = event;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }


}
