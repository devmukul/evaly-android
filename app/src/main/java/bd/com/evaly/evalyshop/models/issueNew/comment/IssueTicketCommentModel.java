package bd.com.evaly.evalyshop.models.issueNew.comment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IssueTicketCommentModel implements Serializable {

    @SerializedName("created_by")
    private CommentedBy commentedBy;

    @SerializedName("ticket")
    private int ticket;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("comment")
    private String comment;

    @SerializedName("id")
    private int id;

    public CommentedBy getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(CommentedBy commentedBy) {
        this.commentedBy = commentedBy;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return
                "IssueTicketCommentModel{" +
                        "commented_by = '" + commentedBy + '\'' +
                        ",ticket = '" + ticket + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",comment = '" + comment + '\'' +
                        ",id = '" + id + '\'' +
                        "}";
    }
}