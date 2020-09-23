package bd.com.evaly.evalyshop.models.image;

import com.google.gson.annotations.SerializedName;

public class Jpeg{

	@SerializedName("quality")
	private int quality;

	public void setQuality(int quality){
		this.quality = quality;
	}

	public int getQuality(){
		return quality;
	}

	@Override
 	public String toString(){
		return 
			"Jpeg{" + 
			"quality = '" + quality + '\'' + 
			"}";
		}
}