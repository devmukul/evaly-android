package bd.com.evaly.evalyshop.models.category;

import com.google.gson.annotations.SerializedName;

public class CategoryItem {

	@SerializedName("image_url")
	private String imageUrl;

	@SerializedName("name")
	private String name;

	@SerializedName("slug")
	private String slug;

	private int drawable = 0;

	public void setImageUrl(String imageUrl){
		this.imageUrl = imageUrl;
	}

	public String getImageUrl(){
		return imageUrl;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setSlug(String slug){
		this.slug = slug;
	}

	public String getSlug(){
		return slug;
	}

	public int getDrawable() {
		return drawable;
	}

	public void setDrawable(int drawable) {
		this.drawable = drawable;
	}

	@Override
 	public String toString(){
		return 
			"CategoryItem{" +
			"image_url = '" + imageUrl + '\'' + 
			",name = '" + name + '\'' + 
			",slug = '" + slug + '\'' + 
			"}";
		}
}