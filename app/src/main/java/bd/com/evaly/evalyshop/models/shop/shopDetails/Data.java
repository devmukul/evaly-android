package bd.com.evaly.evalyshop.models.shop.shopDetails;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data{

	@SerializedName("subscribed")
	private boolean subscribed;

	@SerializedName("shop")
	private Shop shop;

	@SerializedName("subscriber_count")
	private int subscriberCount;

	@SerializedName("groups")
	private List<GroupsItem> groups;

	@SerializedName("items")
	private List<ItemsItem> items;

	@SerializedName("meta")
	private JsonObject meta;

	public void setSubscribed(boolean subscribed){
		this.subscribed = subscribed;
	}

	public boolean isSubscribed(){
		return subscribed;
	}

	public void setShop(Shop shop){
		this.shop = shop;
	}

	public Shop getShop(){
		return shop;
	}

	public void setSubscriberCount(int subscriberCount){
		this.subscriberCount = subscriberCount;
	}

	public int getSubscriberCount(){
		return subscriberCount;
	}

	public void setGroups(List<GroupsItem> groups){
		this.groups = groups;
	}

	public List<GroupsItem> getGroups(){
		return groups;
	}

	public void setItems(List<ItemsItem> items){
		this.items = items;
	}

	public List<ItemsItem> getItems(){
		return items;
	}

	public JsonObject getMeta() {
		return meta;
	}

	public void setMeta(JsonObject meta) {
		this.meta = meta;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"subscribed = '" + subscribed + '\'' + 
			",shop = '" + shop + '\'' + 
			",subscriber_count = '" + subscriberCount + '\'' + 
			",groups = '" + groups + '\'' + 
			",items = '" + items + '\'' + 
			"}";
		}
}