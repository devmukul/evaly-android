package bd.com.evaly.evalyshop.models.catalog.shop;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopItemsResponse {

	@SerializedName("subscribed")
	private boolean subscribed;

	@SerializedName("subscriber_count")
	private int subscriberCount;

	@SerializedName("groups")
	private Object groups;

	@SerializedName("items")
	private List<ShopItem> items;

	public void setSubscribed(boolean subscribed){
		this.subscribed = subscribed;
	}

	public boolean isSubscribed(){
		return subscribed;
	}

	public void setSubscriberCount(int subscriberCount){
		this.subscriberCount = subscriberCount;
	}

	public int getSubscriberCount(){
		return subscriberCount;
	}

	public void setGroups(Object groups){
		this.groups = groups;
	}

	public Object getGroups(){
		return groups;
	}

	public void setItems(List<ShopItem> items){
		this.items = items;
	}

	public List<ShopItem> getItems(){
		return items;
	}

	@Override
 	public String toString(){
		return 
			"ShopDetailsResponse{" + 
			"subscribed = '" + subscribed + '\'' + 
			",subscriber_count = '" + subscriberCount + '\'' + 
			",groups = '" + groups + '\'' + 
			",items = '" + items + '\'' + 
			"}";
		}
}