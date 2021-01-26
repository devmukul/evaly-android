package bd.com.evaly.evalyshop.models.issue;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IssueDetailsModel {

    @SerializedName("order_status")
    private String orderStatus;

    @SerializedName("order_invoice_no")
    private String orderInvoiceNo;

    @SerializedName("shop")
    private String shop;

    @SerializedName("attachment")
    private String attachment;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("issue_replies")
    private List<IssueRepliesItem> issueReplies;

    @SerializedName("issue_type")
    private String issueType;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("description")
    private String description;

    @SerializedName("id")
    private int id;

    @SerializedName("status")
    private String status;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderInvoiceNo() {
        return orderInvoiceNo;
    }

    public void setOrderInvoiceNo(String orderInvoiceNo) {
        this.orderInvoiceNo = orderInvoiceNo;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<IssueRepliesItem> getIssueReplies() {
        return issueReplies;
    }

    public void setIssueReplies(List<IssueRepliesItem> issueReplies) {
        this.issueReplies = issueReplies;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return
                "IssueDetailsModel{" +
                        "order_status = '" + orderStatus + '\'' +
                        ",order_invoice_no = '" + orderInvoiceNo + '\'' +
                        ",shop = '" + shop + '\'' +
                        ",attachment = '" + attachment + '\'' +
                        ",updated_at = '" + updatedAt + '\'' +
                        ",issue_replies = '" + issueReplies + '\'' +
                        ",issue_type = '" + issueType + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",description = '" + description + '\'' +
                        ",id = '" + id + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}