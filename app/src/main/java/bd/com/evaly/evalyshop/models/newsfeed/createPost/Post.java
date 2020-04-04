package bd.com.evaly.evalyshop.models.newsfeed.createPost;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Post implements Serializable {

	@SerializedName("attachment")
	private String attachment;

	@SerializedName("type")
	private String type;

	@SerializedName("body")
	private String body;

	public void setAttachment(String attachment){
		this.attachment = attachment;
	}

	public String getAttachment(){
		return attachment;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setBody(String body){
		this.body = body;
	}

	public String getBody(){
		return body;
	}

	@Override
 	public String toString(){
		return 
			"CreatePostModel{" +
			"attachment = '" + attachment + '\'' + 
			",type = '" + type + '\'' + 
			",body = '" + body + '\'' + 
			"}";
		}
}