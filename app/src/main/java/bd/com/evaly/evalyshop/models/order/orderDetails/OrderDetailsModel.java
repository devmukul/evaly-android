package bd.com.evaly.evalyshop.models.order.orderDetails;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsModel {

	@SerializedName("date")
	private String date;

	@SerializedName("customer_address")
	private String customerAddress;

	@SerializedName("shop")
	private Shop shop;

	@SerializedName("invoice_no")
	private String invoiceNo;

	@SerializedName("payment_status")
	private String paymentStatus;

	@SerializedName("delivery_confirmed")
	private boolean deliveryConfirmed;

	@SerializedName("campaign_rules")
	private List<JsonObject> campaignRules;

	@SerializedName("contact_number")
	private String contactNumber;

	@SerializedName("delivery_confirmation_required")
	private boolean deliveryConfirmationRequired;

	@SerializedName("order_status")
	private String orderStatus;

	@SerializedName("total")
	private String total;

	@SerializedName("shop_groups")
	private List<Object> shopGroups;

	@SerializedName("subtotal")
	private String subtotal;

	@SerializedName("paid_amount")
	private String paidAmount;

	@SerializedName("id")
	private int id;

	@SerializedName("payment_method")
	private String paymentMethod;

	@SerializedName("order_items")
	private List<OrderItemsItem> orderItems;

	@SerializedName("customer")
	private Customer customer;

	@SerializedName("status")
	private String status;

	@SerializedName("customer_note")
	private String customerNote;

	@SerializedName("delivery_charge")
	private String deliveryCharge;

	@SerializedName("apply_delivery_charge")
	private boolean applyDeliveryCharge;

	@SerializedName("allowed_payment_methods")
	private ArrayList<String> allowed_payment_methods;

	public ArrayList<String> getAllowed_payment_methods() {
		return allowed_payment_methods;
	}

	public boolean isApplyDeliveryCharge() {
		return applyDeliveryCharge;
	}

	public String getDeliveryCharge() {
		return deliveryCharge;
	}

	public void setApplyDeliveryCharge(boolean applyDeliveryCharge) {
		this.applyDeliveryCharge = applyDeliveryCharge;
	}

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setCustomerAddress(String customerAddress){
		this.customerAddress = customerAddress;
	}

	public String getCustomerAddress(){
		return customerAddress;
	}

	public void setShop(Shop shop){
		this.shop = shop;
	}

	public Shop getShop(){
		return shop;
	}

	public void setInvoiceNo(String invoiceNo){
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceNo(){
		return invoiceNo;
	}

	public void setPaymentStatus(String paymentStatus){
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentStatus(){
		return paymentStatus;
	}

	public void setDeliveryConfirmed(boolean deliveryConfirmed){
		this.deliveryConfirmed = deliveryConfirmed;
	}

	public boolean isDeliveryConfirmed(){
		return deliveryConfirmed;
	}

	public void setCampaignRules(List<JsonObject> campaignRules){
		this.campaignRules = campaignRules;
	}

	public List<JsonObject> getCampaignRules(){
		return campaignRules;
	}

	public void setContactNumber(String contactNumber){
		this.contactNumber = contactNumber;
	}

	public String getContactNumber(){
		return contactNumber;
	}

	public void setDeliveryConfirmationRequired(boolean deliveryConfirmationRequired){
		this.deliveryConfirmationRequired = deliveryConfirmationRequired;
	}

	public boolean isDeliveryConfirmationRequired(){
		return deliveryConfirmationRequired;
	}

	public void setOrderStatus(String orderStatus){
		this.orderStatus = orderStatus;
	}

	public String getOrderStatus(){
		return orderStatus;
	}

	public void setTotal(String total){
		this.total = total;
	}

	public String getTotal(){
		return total;
	}

	public void setShopGroups(List<Object> shopGroups){
		this.shopGroups = shopGroups;
	}

	public List<Object> getShopGroups(){
		return shopGroups;
	}

	public void setSubtotal(String subtotal){
		this.subtotal = subtotal;
	}

	public String getSubtotal(){
		return subtotal;
	}

	public void setPaidAmount(String paidAmount){
		this.paidAmount = paidAmount;
	}

	public String getPaidAmount(){
		return paidAmount;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setPaymentMethod(String paymentMethod){
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentMethod(){
		return paymentMethod;
	}

	public void setOrderItems(List<OrderItemsItem> orderItems){
		this.orderItems = orderItems;
	}

	public List<OrderItemsItem> getOrderItems(){
		return orderItems;
	}

	public void setCustomer(Customer customer){
		this.customer = customer;
	}

	public Customer getCustomer(){
		return customer;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"OrderDetailsModel{" +
			"date = '" + date + '\'' + 
			",customer_address = '" + customerAddress + '\'' + 
			",shop = '" + shop + '\'' + 
			",invoice_no = '" + invoiceNo + '\'' + 
			",payment_status = '" + paymentStatus + '\'' + 
			",delivery_confirmed = '" + deliveryConfirmed + '\'' + 
			",campaign_rules = '" + campaignRules + '\'' + 
			",contact_number = '" + contactNumber + '\'' + 
			",delivery_confirmation_required = '" + deliveryConfirmationRequired + '\'' + 
			",order_status = '" + orderStatus + '\'' + 
			",total = '" + total + '\'' + 
			",shop_groups = '" + shopGroups + '\'' + 
			",subtotal = '" + subtotal + '\'' + 
			",paid_amount = '" + paidAmount + '\'' + 
			",id = '" + id + '\'' + 
			",payment_method = '" + paymentMethod + '\'' + 
			",order_items = '" + orderItems + '\'' + 
			",customer = '" + customer + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}

	public String getCustomerNote() {
		return customerNote;
	}

	public void setCustomerNote(String customerNote) {
		this.customerNote = customerNote;
	}
}