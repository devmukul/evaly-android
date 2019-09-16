package bd.com.evaly.evalyshop.models.newsfeed.comment;

import com.google.gson.annotations.SerializedName;

public class Author{

	@SerializedName("compressed_image")
	private String compressedImage;

	@SerializedName("full_name")
	private String fullName;

	@SerializedName("username")
	private String username;

	public void setCompressedImage(String compressedImage){
		this.compressedImage = compressedImage;
	}

	public String getCompressedImage(){
		return compressedImage;
	}

	public void setFullName(String fullName){
		this.fullName = fullName;
	}

	public String getFullName(){
		return fullName;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}

	@Override
 	public String toString(){
		return 
			"Author{" + 
			"compressed_image = '" + compressedImage + '\'' + 
			",full_name = '" + fullName + '\'' + 
			",username = '" + username + '\'' + 
			"}";
		}
}