package bd.com.evaly.evalyshop.models.issueNew.comment;

import com.google.gson.annotations.SerializedName;

public class IssueCommentBody {

    @SerializedName("comment")
    private String comment;

    @SerializedName("ticket")
    private int ticket;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }
}
