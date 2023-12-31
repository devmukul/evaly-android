package bd.com.evaly.evalyshop.models.giftcard;


import com.google.gson.annotations.SerializedName;

public class GiftCardListPurchasedItem{

	@SerializedName("createdAt")
	private String createdAt;

	@SerializedName("total")
	private double total;

	@SerializedName("quantity")
	private int quantity;

	@SerializedName("gift_card")
	private String giftCard;

	@SerializedName("invoice_no")
	private String invoiceNo;

	@SerializedName("payment_status")
	private String paymentStatus;

	@SerializedName("gift_status")
	private String giftCardStatus;

	@SerializedName("gift_card_image")
	private Object giftCardImage;

	@SerializedName("from")
	private String from;

	@SerializedName("to")
	private String to;


	@SerializedName("available_balance")
	private double availableBalance;


	@SerializedName("gift_card_price")
	private double giftCardPrice;


	public String getGiftCardStatus() {
		return giftCardStatus;
	}

	public void setGiftCardStatus(String giftCardStatus) {
		this.giftCardStatus = giftCardStatus;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setTotal(int total){
		this.total = total;
	}

	public double getTotal(){
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

	public void setGiftCardImage(Object giftCardImage){
		this.giftCardImage = giftCardImage;
	}

	public Object getGiftCardImage(){
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

	public void setAvailableBalance(int availableBalance){
		this.availableBalance = availableBalance;
	}

	public double getAvailableBalance(){
		return availableBalance;
	}

	public void setGiftCardPrice(int giftCardPrice){
		this.giftCardPrice = giftCardPrice;
	}

	public double getGiftCardPrice(){
		return giftCardPrice;
	}

	@Override
 	public String toString(){
		return 
			"GiftCardListPurchasedItem{" + 
			"createdAt = '" + createdAt + '\'' + 
			",total = '" + total + '\'' + 
			",quantity = '" + quantity + '\'' + 
			",gift_card = '" + giftCard + '\'' + 
			",invoice_no = '" + invoiceNo + '\'' + 
			",payment_status = '" + paymentStatus + '\'' + 
			",gift_card_image = '" + giftCardImage + '\'' + 
			",from = '" + from + '\'' + 
			",to = '" + to + '\'' + 
			",gift_card_price = '" + giftCardPrice + '\'' + 
			"}";
		}
}