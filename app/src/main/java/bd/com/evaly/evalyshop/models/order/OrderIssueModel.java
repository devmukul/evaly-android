package bd.com.evaly.evalyshop.models.order;

public class OrderIssueModel {
    private String issue_type;
    private String description;
    private String attachment;

    public OrderIssueModel(String issue_type, String description) {
        this.issue_type = issue_type;
        this.description = description;
    }

    public OrderIssueModel() {
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getIssue_type() {
        return issue_type;
    }

    public void setIssue_type(String issue_type) {
        this.issue_type = issue_type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
