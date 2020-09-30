package bd.com.evaly.evalyshop.models.image;

import com.google.gson.annotations.SerializedName;

public class CloudFrontRequest{

	@SerializedName("bucket")
	private String bucket;

	@SerializedName("key")
	private String key;

	@SerializedName("edits")
	private Edits edits;

	public void setBucket(String bucket){
		this.bucket = bucket;
	}

	public String getBucket(){
		return bucket;
	}

	public void setEdits(Edits edits){
		this.edits = edits;
	}

	public Edits getEdits(){
		return edits;
	}

	public void setKey(String key){
		this.key = key;
	}

	public String getKey(){
		return key;
	}

	@Override
 	public String toString(){
		return 
			"CloudFrontRequest{" + 
			"bucket = '" + bucket + '\'' + 
			",edits = '" + edits + '\'' + 
			",key = '" + key + '\'' + 
			"}";
		}
}