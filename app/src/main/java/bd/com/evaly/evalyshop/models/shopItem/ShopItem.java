package bd.com.evaly.evalyshop.models.shopItem;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ShopItem{

	@SerializedName("shop_item_name")
	private String shopItemName;

	@SerializedName("attributes")
	private List<AttributesItem> attributes;

	@SerializedName("shop_name")
	private String shopName;

	@SerializedName("shop_image")
	private String shopImage;

	@SerializedName("shop_item_id")
	private int shopItemId;

	@SerializedName("shop_slug")
	private String shopSlug;

	@SerializedName("shop_item_image")
	private String shopItemImage;

	public void setShopItemName(String shopItemName){
		this.shopItemName = shopItemName;
	}

	public String getShopItemName(){
		return shopItemName;
	}

	public void setAttributes(List<AttributesItem> attributes){
		this.attributes = attributes;
	}

	public List<AttributesItem> getAttributes(){
		return attributes;
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

	public void setShopItemId(int shopItemId){
		this.shopItemId = shopItemId;
	}

	public int getShopItemId(){
		return shopItemId;
	}

	public void setShopSlug(String shopSlug){
		this.shopSlug = shopSlug;
	}

	public String getShopSlug(){
		return shopSlug;
	}

	public void setShopItemImage(String shopItemImage){
		this.shopItemImage = shopItemImage;
	}

	public String getShopItemImage(){
		return shopItemImage;
	}

	@Override
 	public String toString(){
		return 
			"ShopItem{" + 
			"shop_item_name = '" + shopItemName + '\'' + 
			",attributes = '" + attributes + '\'' + 
			",shop_name = '" + shopName + '\'' + 
			",shop_image = '" + shopImage + '\'' + 
			",shop_item_id = '" + shopItemId + '\'' + 
			",shop_slug = '" + shopSlug + '\'' + 
			",shop_item_image = '" + shopItemImage + '\'' + 
			"}";
		}
}