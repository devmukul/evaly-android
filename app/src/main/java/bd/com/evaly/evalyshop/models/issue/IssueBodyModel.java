package bd.com.evaly.evalyshop.models.issue;

import com.google.gson.annotations.SerializedName;

public class IssueBodyModel {

    @SerializedName("body")
    private String body;

    @SerializedName("attachment")
    private String attachment;


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
