package bd.com.evaly.evalyshop.models.issueNew.create;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class IssueCreateBody implements Serializable {

    @SerializedName("seller")
    private String seller;

    @SerializedName("shop")
    private String shop;

    @SerializedName("attachments")
    private List<String> attachments;

    @SerializedName("additional_info")
    private String additionalInfo;

    @SerializedName("meta")
    private Meta meta;

    @SerializedName("context")
    private String context;

    @SerializedName("channel")
    private String channel;

    @SerializedName("priority")
    private String priority;

    @SerializedName("category")
    private int category;

    @SerializedName("invoice_no")
    private String invoiceNumber;

    @SerializedName("customer")
    private String customer;

    @SerializedName("answer")
    private Integer answerId;

    @SerializedName("sub_answer")
    private Integer subAnswerId;

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public Integer getSubAnswerId() {
        return subAnswerId;
    }

    public void setSubAnswerId(Integer subAnswerId) {
        this.subAnswerId = subAnswerId;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return
                "IssueCreateBody{" +
                        "seller = '" + seller + '\'' +
                        ",shop = '" + shop + '\'' +
                        ",attachments = '" + attachments + '\'' +
                        ",additional_info = '" + additionalInfo + '\'' +
                        ",meta = '" + meta + '\'' +
                        ",context = '" + context + '\'' +
                        ",channel = '" + channel + '\'' +
                        ",priority = '" + priority + '\'' +
                        ",category = '" + category + '\'' +
                        ",invoice_number = '" + invoiceNumber + '\'' +
                        ",customer = '" + customer + '\'' +
                        "}";
    }
}