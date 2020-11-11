package bd.com.evaly.evalyshop.models.orderRequest;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OrderRequestResponse{

	@SerializedName("customer_address")
	private String customerAddress;

	@SerializedName("note")
	private String note;

	@SerializedName("origin")
	private String origin;

	@SerializedName("invoice_no")
	private String invoiceNo;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("shop_name")
	private String shopName;

	@SerializedName("contact_number")
	private String contactNumber;

	@SerializedName("shop_slug")
	private String shopSlug;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("order_total")
	private int orderTotal;

	@SerializedName("id")
	private String id;

	@SerializedName("items")
	private List<ItemsItem> items;

	@SerializedName("payment_method")
	private String paymentMethod;

	@SerializedName("status")
	private String status;

	@SerializedName("username")
	private String username;

	public void setCustomerAddress(String customerAddress){
		this.customerAddress = customerAddress;
	}

	public String getCustomerAddress(){
		return customerAddress;
	}

	public void setNote(String note){
		this.note = note;
	}

	public String getNote(){
		return note;
	}

	public void setOrigin(String origin){
		this.origin = origin;
	}

	public String getOrigin(){
		return origin;
	}

	public void setInvoiceNo(String invoiceNo){
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceNo(){
		return invoiceNo;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setShopName(String shopName){
		this.shopName = shopName;
	}

	public String getShopName(){
		return shopName;
	}

	public void setContactNumber(String contactNumber){
		this.contactNumber = contactNumber;
	}

	public String getContactNumber(){
		return contactNumber;
	}

	public void setShopSlug(String shopSlug){
		this.shopSlug = shopSlug;
	}

	public String getShopSlug(){
		return shopSlug;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
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

	public void setItems(List<ItemsItem> items){
		this.items = items;
	}

	public List<ItemsItem> getItems(){
		return items;
	}

	public void setPaymentMethod(String paymentMethod){
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentMethod(){
		return paymentMethod;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}

	@Override
 	public String toString(){
		return 
			"OrderRequestResponse{" + 
			"customer_address = '" + customerAddress + '\'' + 
			",note = '" + note + '\'' + 
			",origin = '" + origin + '\'' + 
			",invoice_no = '" + invoiceNo + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",contact_number = '" + contactNumber + '\'' + 
			",shop_slug = '" + shopSlug + '\'' + 
			",updated_at = '" + updatedAt + '\'' + 
			",order_total = '" + orderTotal + '\'' + 
			",id = '" + id + '\'' + 
			",items = '" + items + '\'' + 
			",payment_method = '" + paymentMethod + '\'' + 
			",status = '" + status + '\'' + 
			",username = '" + username + '\'' + 
			"}";
		}
}