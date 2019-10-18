package bd.com.evaly.evalyshop.models.placeOrder;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PlaceOrderItem{

	@SerializedName("customer_address")
	private String customerAddress;

	@SerializedName("payment_method")
	private String paymentMethod;

	@SerializedName("contact_number")
	private String contactNumber;

	@SerializedName("order_items")
	private List<OrderItemsItem> orderItems;

	public void setCustomerAddress(String customerAddress){
		this.customerAddress = customerAddress;
	}

	public String getCustomerAddress(){
		return customerAddress;
	}

	public void setPaymentMethod(String paymentMethod){
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentMethod(){
		return paymentMethod;
	}

	public void setContactNumber(String contactNumber){
		this.contactNumber = contactNumber;
	}

	public String getContactNumber(){
		return contactNumber;
	}

	public void setOrderItems(List<OrderItemsItem> orderItems){
		this.orderItems = orderItems;
	}

	public List<OrderItemsItem> getOrderItems(){
		return orderItems;
	}

	@Override
 	public String toString(){
		return 
			"PlaceOrderItem{" + 
			"customer_address = '" + customerAddress + '\'' + 
			",payment_method = '" + paymentMethod + '\'' + 
			",contact_number = '" + contactNumber + '\'' + 
			",order_items = '" + orderItems + '\'' + 
			"}";
		}
}