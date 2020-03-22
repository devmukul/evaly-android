package bd.com.evaly.evalyshop.models.shop.shopGroup;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopGroupResponse{

	@SerializedName("shops")
	private List<ShopsItem> shops;

	@SerializedName("name")
	private String name;

	@SerializedName("banner")
	private Object banner;

	@SerializedName("id")
	private int id;

	@SerializedName("slug")
	private String slug;

	public void setShops(List<ShopsItem> shops){
		this.shops = shops;
	}

	public List<ShopsItem> getShops(){
		return shops;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setBanner(Object banner){
		this.banner = banner;
	}

	public Object getBanner(){
		return banner;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	@Override
 	public String toString(){
		return 
			"ShopGroupResponse{" + 
			"shops = '" + shops + '\'' + 
			",name = '" + name + '\'' + 
			",banner = '" + banner + '\'' + 
			",id = '" + id + '\'' + 
			",slug = '" + slug + '\'' + 
			"}";
		}
}