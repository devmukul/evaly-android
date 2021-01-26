package bd.com.evaly.evalyshop.models.issue;


import com.google.gson.annotations.SerializedName;

public class IssueRepliesItem {

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("reply_by")
    private ReplyBy replyBy;

    @SerializedName("attachement")
    private Object attachement;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("id")
    private int id;

    @SerializedName("body")
    private String body;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ReplyBy getReplyBy() {
        return replyBy;
    }

    public void setReplyBy(ReplyBy replyBy) {
        this.replyBy = replyBy;
    }

    public Object getAttachement() {
        return attachement;
    }

    public void setAttachement(Object attachement) {
        this.attachement = attachement;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return
                "IssueRepliesItem{" +
                        "updated_at = '" + updatedAt + '\'' +
                        ",reply_by = '" + replyBy + '\'' +
                        ",attachement = '" + attachement + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",id = '" + id + '\'' +
                        ",body = '" + body + '\'' +
                        "}";
    }
}