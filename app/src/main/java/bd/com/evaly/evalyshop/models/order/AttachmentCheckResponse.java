package bd.com.evaly.evalyshop.models.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttachmentCheckResponse {

	@SerializedName("attachment_required")
	private boolean attachmentRequired;

	@SerializedName("shop_name")
	private String shopName;

	@SerializedName("shop_image")
	private String shopImage;

	@SerializedName("items")
	private List<Integer> items;

	@SerializedName("shop_slug")
	private String shopSlug;

	public void setAttachmentRequired(boolean attachmentRequired){
		this.attachmentRequired = attachmentRequired;
	}

	public boolean isAttachmentRequired(){
		return attachmentRequired;
	}

	public void setShopName(String shopName){
		this.shopName = shopName;
	}

	public String getShopName(){
		return shopName;
	}

	public void setShopImage(String shopImage){
		this.shopImage = shopImage;
	}

	public String getShopImage(){
		return shopImage;
	}

	public void setItems(List<Integer> items){
		this.items = items;
	}

	public List<Integer> getItems(){
		return items;
	}

	public void setShopSlug(String shopSlug){
		this.shopSlug = shopSlug;
	}

	public String getShopSlug(){
		return shopSlug;
	}

	@Override
 	public String toString(){
		return 
			"CheckoutItemResponse{" + 
			"attachment_required = '" + attachmentRequired + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",shop_image = '" + shopImage + '\'' + 
			",items = '" + items + '\'' + 
			",shop_slug = '" + shopSlug + '\'' + 
			"}";
		}
}