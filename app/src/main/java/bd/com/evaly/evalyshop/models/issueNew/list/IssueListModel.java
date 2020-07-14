package bd.com.evaly.evalyshop.models.issueNew.list;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class IssueListModel implements Serializable {

	@SerializedName("shop")
	private String shop;

	@SerializedName("attachments")
	private List<String> attachments;

	@SerializedName("channel")
	private String channel;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("priority")
	private String priority;

	@SerializedName("created_by")
	private CreatedBy createdBy;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("additional_info")
	private String additionalInfo;

	@SerializedName("context")
	private String context;

	@SerializedName("updated_by")
	private UpdatedBy updatedBy;

	@SerializedName("id")
	private int id;

	@SerializedName("category")
	private Category category;

	@SerializedName("invoice_number")
	private String invoiceNumber;

	@SerializedName("status")
	private String status;

	@SerializedName("customer")
	private String customer;

	@SerializedName("assigned_to")
	private String assignedTo;

	public void setShop(String shop){
		this.shop = shop;
	}

	public String getShop(){
		return shop;
	}

	public void setAttachments(List<String> attachments){
		this.attachments = attachments;
	}

	public List<String> getAttachments(){
		return attachments;
	}

	public void setChannel(String channel){
		this.channel = channel;
	}

	public String getChannel(){
		return channel;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setPriority(String priority){
		this.priority = priority;
	}

	public String getPriority(){
		return priority;
	}

	public void setCreatedBy(CreatedBy createdBy){
		this.createdBy = createdBy;
	}

	public CreatedBy getCreatedBy(){
		return createdBy;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setAdditionalInfo(String additionalInfo){
		this.additionalInfo = additionalInfo;
	}

	public String getAdditionalInfo(){
		return additionalInfo;
	}

	public void setContext(String context){
		this.context = context;
	}

	public String getContext(){
		return context;
	}

	public void setUpdatedBy(UpdatedBy updatedBy){
		this.updatedBy = updatedBy;
	}

	public UpdatedBy getUpdatedBy(){
		return updatedBy;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setCategory(Category category){
		this.category = category;
	}

	public Category getCategory(){
		return category;
	}

	public void setInvoiceNumber(String invoiceNumber){
		this.invoiceNumber = invoiceNumber;
	}

	public String getInvoiceNumber(){
		return invoiceNumber;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setCustomer(String customer){
		this.customer = customer;
	}

	public String getCustomer(){
		return customer;
	}

	public void setAssignedTo(String assignedTo){
		this.assignedTo = assignedTo;
	}

	public String getAssignedTo(){
		return assignedTo;
	}

	@Override
	public String toString(){
		return
				"Response{" +
						"shop = '" + shop + '\'' +
						",attachments = '" + attachments + '\'' +
						",channel = '" + channel + '\'' +
						",created_at = '" + createdAt + '\'' +
						",priority = '" + priority + '\'' +
						",created_by = '" + createdBy + '\'' +
						",updated_at = '" + updatedAt + '\'' +
						",additional_info = '" + additionalInfo + '\'' +
						",context = '" + context + '\'' +
						",updated_by = '" + updatedBy + '\'' +
						",id = '" + id + '\'' +
						",category = '" + category + '\'' +
						",invoice_number = '" + invoiceNumber + '\'' +
						",status = '" + status + '\'' +
						",customer = '" + customer + '\'' +
						",assigned_to = '" + assignedTo + '\'' +
						"}";
	}
}