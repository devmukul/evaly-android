package bd.com.evaly.evalyshop.models.notification;


import com.google.gson.annotations.SerializedName;

public class NotificationItem {

	@SerializedName("thumb_url")
	private String thumbUrl;

	@SerializedName("read")
	private boolean read;

	@SerializedName("web_url")
	private String webUrl;

	@SerializedName("content_type")
	private String contentType;

	@SerializedName("content_id")
	private String contentId;

	@SerializedName("meta_data")
	private String metaData;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("content_url")
	private String contentUrl;

	@SerializedName("id")
	private int id;

	@SerializedName("message")
	private String message;

	@SerializedName("user")
	private int user;

	public void setThumbUrl(String thumbUrl){
		this.thumbUrl = thumbUrl;
	}

	public String getThumbUrl(){
		return thumbUrl;
	}

	public void setRead(boolean read){
		this.read = read;
	}

	public boolean isRead(){
		return read;
	}

	public void setWebUrl(String webUrl){
		this.webUrl = webUrl;
	}

	public String getWebUrl(){
		return webUrl;
	}

	public void setContentType(String contentType){
		this.contentType = contentType;
	}

	public String getContentType(){
		return contentType;
	}

	public void setContentId(String contentId){
		this.contentId = contentId;
	}

	public String getContentId(){
		return contentId;
	}

	public void setMetaData(String metaData){
		this.metaData = metaData;
	}

	public String getMetaData(){
		return metaData;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setContentUrl(String contentUrl){
		this.contentUrl = contentUrl;
	}

	public String getContentUrl(){
		return contentUrl;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setUser(int user){
		this.user = user;
	}

	public int getUser(){
		return user;
	}

	@Override
 	public String toString(){
		return 
			"NotificationItem{" + 
			"thumb_url = '" + thumbUrl + '\'' + 
			",read = '" + read + '\'' + 
			",web_url = '" + webUrl + '\'' + 
			",content_type = '" + contentType + '\'' + 
			",content_id = '" + contentId + '\'' + 
			",meta_data = '" + metaData + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",content_url = '" + contentUrl + '\'' + 
			",id = '" + id + '\'' + 
			",message = '" + message + '\'' + 
			",user = '" + user + '\'' + 
			"}";
		}
}