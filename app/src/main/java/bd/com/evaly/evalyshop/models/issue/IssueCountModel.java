package bd.com.evaly.evalyshop.models.issue;


import com.google.gson.annotations.SerializedName;

public class IssueCountModel {

    @SerializedName("active")
    private int active;

    @SerializedName("closed")
    private int closed;

    @SerializedName("type")
    private String type;

    @SerializedName("resolved")
    private int resolved;

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getClosed() {
        return closed;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getResolved() {
        return resolved;
    }

    public void setResolved(int resolved) {
        this.resolved = resolved;
    }

    @Override
    public String toString() {
        return
                "IssueCountModel{" +
                        "active = '" + active + '\'' +
                        ",closed = '" + closed + '\'' +
                        ",type = '" + type + '\'' +
                        ",resolved = '" + resolved + '\'' +
                        "}";
    }
}