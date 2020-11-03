package bd.com.evaly.evalyshop.models.search;

import com.google.gson.annotations.SerializedName;

public class HighlightResult{

	@SerializedName("name")
	private Name name;

	public void setName(Name name){
		this.name = name;
	}

	public Name getName(){
		return name;
	}

	@Override
 	public String toString(){
		return 
			"HighlightResult{" + 
			"name = '" + name + '\'' + 
			"}";
		}
}