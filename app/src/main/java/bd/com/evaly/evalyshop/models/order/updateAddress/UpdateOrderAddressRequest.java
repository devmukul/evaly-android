package bd.com.evaly.evalyshop.models.order.updateAddress;

import com.google.gson.annotations.SerializedName;

public class UpdateOrderAddressRequest{

	@SerializedName("note")
	private String note;

	@SerializedName("address")
	private String address;

	@SerializedName("invoice_no")
	private String invoiceNo;

	public void setNote(String note){
		this.note = note;
	}

	public String getNote(){
		return note;
	}

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setInvoiceNo(String invoiceNo){
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceNo(){
		return invoiceNo;
	}

	@Override
 	public String toString(){
		return 
			"UpdateOrderAddressRequest{" + 
			"note = '" + note + '\'' + 
			",address = '" + address + '\'' + 
			",invoice_no = '" + invoiceNo + '\'' + 
			"}";
		}
}