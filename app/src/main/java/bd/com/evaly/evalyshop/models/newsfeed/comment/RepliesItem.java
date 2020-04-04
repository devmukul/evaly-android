package bd.com.evaly.evalyshop.models.newsfeed.comment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RepliesItem implements Serializable {

	@SerializedName("author")
	private Author author;

	@SerializedName("attachement")
	private Object attachement;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("body")
	private String body;

	public void setAuthor(Author author){
		this.author = author;
	}

	public Author getAuthor(){
		return author;
	}

	public void setAttachement(Object attachement){
		this.attachement = attachement;
	}

	public Object getAttachement(){
		return attachement;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
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
			"RepliesItem{" + 
			"author = '" + author + '\'' + 
			",attachement = '" + attachement + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",id = '" + id + '\'' + 
			",body = '" + body + '\'' + 
			"}";
		}
}