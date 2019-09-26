package bd.com.evaly.evalyshop.models.newsfeed.newsfeed;

import com.google.gson.annotations.SerializedName;

public class NewsfeedPost{

	@SerializedName("author_compressed_image")
	private String authorCompressedImage;

	@SerializedName("favorites_count")
	private int favoritesCount;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("author_full_name")
	private String authorFullName;

	@SerializedName("body")
	private String body;

	@SerializedName("type")
	private String type;

	@SerializedName("author_image")
	private String authorImage;

	@SerializedName("attachment_compressed_url")
	private String attachmentCompressedUrl;

	@SerializedName("attachment")
	private String attachment;

	@SerializedName("comments_count")
	private int commentsCount;

	@SerializedName("author_is_admin")
	private int authorIsAdmin;

	@SerializedName("id")
	private int id;

	@SerializedName("author_id")
	private int authorId;

	@SerializedName("slug")
	private String slug;

	@SerializedName("status")
	private String status;

	@SerializedName("favorited")
	private int favorited;

	@SerializedName("username")
	private int username;

	public void setAuthorCompressedImage(String authorCompressedImage){
		this.authorCompressedImage = authorCompressedImage;
	}

	public String getAuthorCompressedImage(){
		return authorCompressedImage;
	}

	public void setFavoritesCount(int favoritesCount){
		this.favoritesCount = favoritesCount;
	}

	public int getFavoritesCount(){
		return favoritesCount;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setAuthorFullName(String authorFullName){
		this.authorFullName = authorFullName;
	}

	public String getAuthorFullName(){
		return authorFullName;
	}

	public void setBody(String body){
		this.body = body;
	}

	public String getBody(){
		return body;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setAuthorImage(String authorImage){
		this.authorImage = authorImage;
	}

	public String getAuthorImage(){
		return authorImage;
	}

	public void setAttachmentCompressedUrl(String attachmentCompressedUrl){
		this.attachmentCompressedUrl = attachmentCompressedUrl;
	}

	public String getAttachmentCompressedUrl(){
		return attachmentCompressedUrl;
	}

	public void setAttachment(String attachment){
		this.attachment = attachment;
	}

	public String getAttachment(){
		return attachment;
	}

	public void setCommentsCount(int commentsCount){
		this.commentsCount = commentsCount;
	}

	public int getCommentsCount(){
		return commentsCount;
	}

	public void setAuthorIsAdmin(int authorIsAdmin){
		this.authorIsAdmin = authorIsAdmin;
	}

	public int getAuthorIsAdmin(){
		return authorIsAdmin;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setAuthorId(int authorId){
		this.authorId = authorId;
	}

	public int getAuthorId(){
		return authorId;
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

	public void setFavorited(int favorited){
		this.favorited = favorited;
	}

	public int getFavorited(){
		return favorited;
	}

	public void setUsername(int username){
		this.username = username;
	}

	public int getUsername(){
		return username;
	}

	@Override
 	public String toString(){
		return 
			"NewsfeedPost{" + 
			"author_compressed_image = '" + authorCompressedImage + '\'' + 
			",favorites_count = '" + favoritesCount + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",author_full_name = '" + authorFullName + '\'' + 
			",body = '" + body + '\'' + 
			",type = '" + type + '\'' + 
			",author_image = '" + authorImage + '\'' + 
			",attachment_compressed_url = '" + attachmentCompressedUrl + '\'' + 
			",attachment = '" + attachment + '\'' + 
			",comments_count = '" + commentsCount + '\'' + 
			",author_is_admin = '" + authorIsAdmin + '\'' + 
			",id = '" + id + '\'' + 
			",author_id = '" + authorId + '\'' + 
			",slug = '" + slug + '\'' + 
			",status = '" + status + '\'' + 
			",favorited = '" + favorited + '\'' + 
			",username = '" + username + '\'' + 
			"}";
		}
}