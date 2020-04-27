package bd.com.evaly.evalyshop.models.order.placeOrder;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceOrderItem {

    @SerializedName("customer_address")
    private String customerAddress;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("contact_number")
    private String contactNumber;

    @SerializedName("order_origin")
    private String orderOrigin;

    @SerializedName("delivery_lat")
    private String deliveryLatitude;

    @SerializedName("delivery_lon")
    private String deliveryLongitude;

    @SerializedName("order_items")
    private List<OrderItemsItem> orderItems;

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public List<OrderItemsItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemsItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getOrderOrigin() {
        return orderOrigin;
    }

    public void setOrderOrigin(String orderOrigin) {
        this.orderOrigin = orderOrigin;
    }

	public String getDeliveryLatitude() {
		return deliveryLatitude;
	}

	public void setDeliveryLatitude(String deliveryLatitude) {
		this.deliveryLatitude = deliveryLatitude;
	}

	public String getDeliveryLongitude() {
		return deliveryLongitude;
	}

	public void setDeliveryLongitude(String deliveryLongitude) {
		this.deliveryLongitude = deliveryLongitude;
	}

	@Override
    public String toString() {
        return
                "PlaceOrderItem{" +
                        "customer_address = '" + customerAddress + '\'' +
                        ",payment_method = '" + paymentMethod + '\'' +
                        ",contact_number = '" + contactNumber + '\'' +
                        ",order_items = '" + orderItems + '\'' +
                        "}";
    }
}