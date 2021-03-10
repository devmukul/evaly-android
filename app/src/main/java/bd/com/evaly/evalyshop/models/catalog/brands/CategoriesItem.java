package bd.com.evaly.evalyshop.models.catalog.brands;

import com.google.gson.annotations.SerializedName;

public class CategoriesItem{

	@SerializedName("parent")
	private String parent;

	@SerializedName("score")
	private int score;

	@SerializedName("approved")
	private boolean approved;

	@SerializedName(value = "image_url", alternate = "category_image")
	private String imageUrl;

	@SerializedName(value = "name", alternate = "category_name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("id")
	private int id;

	@SerializedName(value = "slug", alternate = "category_slug")
	private String slug;

	@SerializedName("status")
	private String status;

	public void setParent(String parent){
		this.parent = parent;
	}

	public String getParent(){
		return parent;
	}

	public void setScore(int score){
		this.score = score;
	}

	public int getScore(){
		return score;
	}

	public void setApproved(boolean approved){
		this.approved = approved;
	}

	public boolean isApproved(){
		return approved;
	}

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

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
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

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"CategoriesItem{" + 
			"parent = '" + parent + '\'' + 
			",score = '" + score + '\'' + 
			",approved = '" + approved + '\'' + 
			",image_url = '" + imageUrl + '\'' + 
			",name = '" + name + '\'' + 
			",description = '" + description + '\'' + 
			",id = '" + id + '\'' + 
			",slug = '" + slug + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}