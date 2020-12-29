package bd.com.evaly.evalyshop.models.catalog.category;

import com.google.gson.annotations.SerializedName;

public class ChildCategoryResponse{

	@SerializedName("parent")
	private String parent;

	@SerializedName("score")
	private double score;

	@SerializedName("approved")
	private boolean approved;

	@SerializedName("image_url")
	private String imageUrl;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("id")
	private int id;

	@SerializedName("slug")
	private String slug;

	@SerializedName("status")
	private String status;

	public void setParent(String parent){
		this.parent = parent;
	}

	public String getParent(){
		return parent;
	}

	public void setScore(double score){
		this.score = score;
	}

	public double getScore(){
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
			"ChildCategoryResponse{" + 
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