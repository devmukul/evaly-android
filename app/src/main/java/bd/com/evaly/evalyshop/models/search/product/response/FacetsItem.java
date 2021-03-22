package bd.com.evaly.evalyshop.models.search.product.response;

import com.google.gson.annotations.SerializedName;

public class FacetsItem {

	@SerializedName("doc_count")
	private int docCount;

	@SerializedName("key")
	private String key;

	public void setDocCount(int docCount){
		this.docCount = docCount;
	}

	public int getDocCount(){
		return docCount;
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
			"BrandsItem{" + 
			"doc_count = '" + docCount + '\'' + 
			",key = '" + key + '\'' + 
			"}";
		}
}