package bd.com.evaly.evalyshop.models.user;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Addresses{

	@SerializedName("data")
	private List<AddressItem> data;

	public void setData(List<AddressItem> data){
		this.data = data;
	}

	public List<AddressItem> getData(){
		return data;
	}

	@Override
 	public String toString(){
		return 
			"Addresses{" + 
			"data = '" + data + '\'' + 
			"}";
		}
}