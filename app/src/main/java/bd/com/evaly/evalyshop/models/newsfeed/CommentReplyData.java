package bd.com.evaly.evalyshop.models.newsfeed;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import bd.com.evaly.evalyshop.models.newsfeed.comment.CommentItem;


public class CommentReplyData implements Serializable {

    @SerializedName("data")
    private CommentItem data;

    public CommentItem getData() {
        return data;
    }

    public void setData(CommentItem data) {
        this.data = data;
    }
}
