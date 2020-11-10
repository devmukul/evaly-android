package bd.com.evaly.evalyshop.models.orderRequest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderRequestResponse{

	@SerializedName("request_status")
	private String requestStatus;

	@SerializedName("customer_address")
	private String customerAddress;

	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("invoice_no")
	private String invoiceNo;

	@SerializedName("order_total")
	private int orderTotal;

	@SerializedName("_id")
	private String id;

	@SerializedName("shop_name")
	private String shopName;

	@SerializedName("request_id")
	private String requestId;

	@SerializedName("contact_number")
	private String contactNumber;

	@SerializedName("username")
	private String username;

	@SerializedName("order_items")
	private List<OrderItemsItem> orderItems;

	@SerializedName("updatedAt")
	private String updatedAt;

	public void setRequestStatus(String requestStatus){
		this.requestStatus = requestStatus;
	}

	public String getRequestStatus(){
		return requestStatus;
	}

	public void setCustomerAddress(String customerAddress){
		this.customerAddress = customerAddress;
	}

	public String getCustomerAddress(){
		return customerAddress;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setInvoiceNo(String invoiceNo){
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceNo(){
		return invoiceNo;
	}

	public void setOrderTotal(int orderTotal){
		this.orderTotal = orderTotal;
	}

	public int getOrderTotal(){
		return orderTotal;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setShopName(String shopName){
		this.shopName = shopName;
	}

	public String getShopName(){
		return shopName;
	}

	public void setRequestId(String requestId){
		this.requestId = requestId;
	}

	public String getRequestId(){
		return requestId;
	}

	public void setContactNumber(String contactNumber){
		this.contactNumber = contactNumber;
	}

	public String getContactNumber(){
		return contactNumber;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}

	public void setOrderItems(List<OrderItemsItem> orderItems){
		this.orderItems = orderItems;
	}

	public List<OrderItemsItem> getOrderItems(){
		return orderItems;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	@Override
 	public String toString(){
		return 
			"OrderRequestResponse{" + 
			"request_status = '" + requestStatus + '\'' + 
			",customer_address = '" + customerAddress + '\'' + 
			",createdAt = '" + createdAt + '\'' + 
			",invoice_no = '" + invoiceNo + '\'' + 
			",order_total = '" + orderTotal + '\'' + 
			",_id = '" + id + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",request_id = '" + requestId + '\'' + 
			",contact_number = '" + contactNumber + '\'' + 
			",username = '" + username + '\'' + 
			",order_items = '" + orderItems + '\'' + 
			",updatedAt = '" + updatedAt + '\'' + 
			"}";
		}
}