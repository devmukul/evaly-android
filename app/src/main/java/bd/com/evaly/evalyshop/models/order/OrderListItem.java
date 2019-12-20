package bd.com.evaly.evalyshop.models.order;

import com.google.gson.annotations.SerializedName;

public class OrderListItem{

	@SerializedName("date")
	private String date;

	@SerializedName("order_status")
	private String orderStatus;

	@SerializedName("total")
	private String total;

	@SerializedName("invoice_no")
	private String invoiceNo;

	@SerializedName("payment_status")
	private String paymentStatus;

	@SerializedName("id")
	private int id;

	@SerializedName("payment_method")
	private String paymentMethod;

	@SerializedName("customer")
	private Customer customer;

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
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

	public void setCustomer(Customer customer){
		this.customer = customer;
	}

	public Customer getCustomer(){
		return customer;
	}

	@Override
 	public String toString(){
		return 
			"OrderListItem{" + 
			"date = '" + date + '\'' + 
			",order_status = '" + orderStatus + '\'' + 
			",total = '" + total + '\'' + 
			",invoice_no = '" + invoiceNo + '\'' + 
			",payment_status = '" + paymentStatus + '\'' + 
			",id = '" + id + '\'' + 
			",payment_method = '" + paymentMethod + '\'' + 
			",customer = '" + customer + '\'' + 
			"}";
		}
}