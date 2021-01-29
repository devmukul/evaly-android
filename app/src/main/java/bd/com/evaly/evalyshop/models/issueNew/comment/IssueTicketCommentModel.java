package bd.com.evaly.evalyshop.models.issueNew.comment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IssueTicketCommentModel implements Serializable {

    @SerializedName("commented_by")
    private CommentedBy commentedBy;

    @SerializedName("ticket")
    private Ticket ticket;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("comment")
    private String comment;

    @SerializedName("id")
    private int id;

    public void setCommentedBy(CommentedBy commentedBy){
        this.commentedBy = commentedBy;
    }

    public CommentedBy getCommentedBy(){
        return commentedBy;
    }

    public void setTicket(Ticket ticket){
        this.ticket = ticket;
    }

    public Ticket getTicket(){
        return ticket;
    }

    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public String getComment(){
        return comment;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    @Override
    public String toString(){
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