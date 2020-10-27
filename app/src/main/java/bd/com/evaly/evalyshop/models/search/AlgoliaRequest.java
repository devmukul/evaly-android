package bd.com.evaly.evalyshop.models.search;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AlgoliaRequest{

	@SerializedName("requests")
	private List<RequestsItem> requests;

	public void setRequests(List<RequestsItem> requests){
		this.requests = requests;
	}

	public List<RequestsItem> getRequests(){
		return requests;
	}

	@Override
 	public String toString(){
		return 
			"AlgoliaRequest{" + 
			"requests = '" + requests + '\'' + 
			"}";
		}
}