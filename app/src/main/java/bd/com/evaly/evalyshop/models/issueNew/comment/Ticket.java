package bd.com.evaly.evalyshop.models.issueNew.comment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Ticket implements Serializable {

    @SerializedName("shop")
    private Shop shop;

    @SerializedName("attachments")
    private List<String> attachments;

    @SerializedName("channel")
    private String channel;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("priority")
    private String priority;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("additional_info")
    private String additionalInfo;

    @SerializedName("context")
    private String context;

    @SerializedName("id")
    private int id;

    @SerializedName("category")
    private Category category;

    @SerializedName("invoice_number")
    private String invoiceNumber;

    @SerializedName("status")
    private String status;

    @SerializedName("assigned_to")
    private AssignedTo assignedTo;

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AssignedTo getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(AssignedTo assignedTo) {
        this.assignedTo = assignedTo;
    }

    @Override
    public String toString() {
        return
                "Ticket{" +
                        "shop = '" + shop + '\'' +
                        ",attachments = '" + attachments + '\'' +
                        ",channel = '" + channel + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",priority = '" + priority + '\'' +
                        ",updated_at = '" + updatedAt + '\'' +
                        ",additional_info = '" + additionalInfo + '\'' +
                        ",context = '" + context + '\'' +
                        ",id = '" + id + '\'' +
                        ",category = '" + category + '\'' +
                        ",invoice_number = '" + invoiceNumber + '\'' +
                        ",status = '" + status + '\'' +
                        ",assigned_to = '" + assignedTo + '\'' +
                        "}";
    }
}