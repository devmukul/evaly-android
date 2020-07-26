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

	@SerializedName("invoice_number")
	private String invoiceNumber;

	@SerializedName("customer")
	private String customer;

	public void setSeller(String seller){
		this.seller = seller;
	}

	public String getSeller(){
		return seller;
	}

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

	public void setAdditionalInfo(String additionalInfo){
		this.additionalInfo = additionalInfo;
	}

	public String getAdditionalInfo(){
		return additionalInfo;
	}

	public void setMeta(Meta meta){
		this.meta = meta;
	}

	public Meta getMeta(){
		return meta;
	}

	public void setContext(String context){
		this.context = context;
	}

	public String getContext(){
		return context;
	}

	public void setChannel(String channel){
		this.channel = channel;
	}

	public String getChannel(){
		return channel;
	}

	public void setPriority(String priority){
		this.priority = priority;
	}

	public String getPriority(){
		return priority;
	}

	public void setCategory(int category){
		this.category = category;
	}

	public int getCategory(){
		return category;
	}

	public void setInvoiceNumber(String invoiceNumber){
		this.invoiceNumber = invoiceNumber;
	}

	public String getInvoiceNumber(){
		return invoiceNumber;
	}

	public void setCustomer(String customer){
		this.customer = customer;
	}

	public String getCustomer(){
		return customer;
	}

	@Override
 	public String toString(){
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