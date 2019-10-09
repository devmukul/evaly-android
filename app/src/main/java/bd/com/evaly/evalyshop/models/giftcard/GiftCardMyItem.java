package bd.com.evaly.evalyshop.models.giftcard;


import com.google.gson.annotations.SerializedName;


public class GiftCardMyItem{

	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("total")
	private int total;

	@SerializedName("quantity")
	private int quantity;

	@SerializedName("gift_card")
	private String giftCard;

	@SerializedName("invoice_no")
	private String invoiceNo;

	@SerializedName("payment_status")
	private String paymentStatus;

	@SerializedName("gift_card_image")
	private String giftCardImage;

	@SerializedName("from")
	private String from;

	@SerializedName("to")
	private String to;

	@SerializedName("gift_status")
	private String giftStatus;

	@SerializedName("available_balance")
	private int availableBalance;

	@SerializedName("gift_card_price")
	private int giftCardPrice;

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setTotal(int total){
		this.total = total;
	}

	public int getTotal(){
		return total;
	}

	public void setQuantity(int quantity){
		this.quantity = quantity;
	}

	public int getQuantity(){
		return quantity;
	}

	public void setGiftCard(String giftCard){
		this.giftCard = giftCard;
	}

	public String getGiftCard(){
		return giftCard;
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

	public void setGiftCardImage(String giftCardImage){
		this.giftCardImage = giftCardImage;
	}

	public String getGiftCardImage(){
		return giftCardImage;
	}

	public void setFrom(String from){
		this.from = from;
	}

	public String getFrom(){
		return from;
	}

	public void setTo(String to){
		this.to = to;
	}

	public String getTo(){
		return to;
	}

	public void setGiftStatus(String giftStatus){
		this.giftStatus = giftStatus;
	}

	public String getGiftStatus(){
		return giftStatus;
	}

	public void setAvailableBalance(int availableBalance){
		this.availableBalance = availableBalance;
	}

	public int getAvailableBalance(){
		return availableBalance;
	}

	public void setGiftCardPrice(int giftCardPrice){
		this.giftCardPrice = giftCardPrice;
	}

	public int getGiftCardPrice(){
		return giftCardPrice;
	}

	@Override
 	public String toString(){
		return 
			"GiftCardMyItem{" + 
			"createdAt = '" + createdAt + '\'' + 
			",total = '" + total + '\'' + 
			",quantity = '" + quantity + '\'' + 
			",gift_card = '" + giftCard + '\'' + 
			",invoice_no = '" + invoiceNo + '\'' + 
			",payment_status = '" + paymentStatus + '\'' + 
			",gift_card_image = '" + giftCardImage + '\'' + 
			",from = '" + from + '\'' + 
			",to = '" + to + '\'' + 
			",gift_status = '" + giftStatus + '\'' + 
			",available_balance = '" + availableBalance + '\'' + 
			",gift_card_price = '" + giftCardPrice + '\'' + 
			"}";
		}
}