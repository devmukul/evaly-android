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
}
