package bd.com.evaly.evalyshop.models.chat;

public class EvalyUserModel {
    private int id;
    private String username;
    private String first_name;
    private String last_name;
    private boolean is_active;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }
}
