package bd.com.evaly.evalyshop.models.newsfeed.comment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CommentItem implements Serializable {

	@SerializedName("attachment_compressed_url")
	private Object attachmentCompressedUrl;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("replies")
	private List<RepliesItem> replies;

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

	public void setAttachmentCompressedUrl(Object attachmentCompressedUrl){
		this.attachmentCompressedUrl = attachmentCompressedUrl;
	}

	public Object getAttachmentCompressedUrl(){
		return attachmentCompressedUrl;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setReplies(List<RepliesItem> replies){
		this.replies = replies;
	}

	public List<RepliesItem> getReplies(){
		return replies;
	}

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

	public static DiffUtil.ItemCallback<CommentItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<CommentItem>() {
		@Override
		public boolean areItemsTheSame(@NonNull CommentItem oldItem, @NonNull CommentItem newItem) {
			return oldItem.id == newItem.id;
		}

		@Override
		public boolean areContentsTheSame(@NonNull CommentItem oldItem, @NonNull CommentItem newItem) {
			return oldItem.equals(newItem);
		}
	};

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		CommentItem article = (CommentItem) obj;
		return article.id == this.id;
	}

	@Override
 	public String toString(){
		return 
			"CommentItem{" + 
			"attachment_compressed_url = '" + attachmentCompressedUrl + '\'' + 
			",updated_at = '" + updatedAt + '\'' + 
			",replies = '" + replies + '\'' + 
			",author = '" + author + '\'' + 
			",attachement = '" + attachement + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",id = '" + id + '\'' + 
			",body = '" + body + '\'' + 
			"}";
		}
}