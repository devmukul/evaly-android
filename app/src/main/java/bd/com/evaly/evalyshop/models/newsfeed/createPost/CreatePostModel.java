package bd.com.evaly.evalyshop.models.newsfeed.createPost;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreatePostModel implements Serializable {

	@SerializedName("post")
	private Post post;

	public void setPost(Post post){
		this.post = post;
	}

	public Post getPost(){
		return post;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
			"post = '" + post + '\'' + 
			"}";
		}
}