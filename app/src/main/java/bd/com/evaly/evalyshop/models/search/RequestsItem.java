package bd.com.evaly.evalyshop.models.search;

import com.google.gson.annotations.SerializedName;

public class RequestsItem{

	@SerializedName("indexName")
	private String indexName;

	@SerializedName("params")
	private String params;

	public void setIndexName(String indexName){
		this.indexName = indexName;
	}

	public String getIndexName(){
		return indexName;
	}

	public void setParams(String params){
		this.params = params;
	}

	public String getParams(){
		return params;
	}

	@Override
 	public String toString(){
		return 
			"RequestsItem{" + 
			"indexName = '" + indexName + '\'' + 
			",params = '" + params + '\'' + 
			"}";
		}
}