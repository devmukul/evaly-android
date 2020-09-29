package bd.com.evaly.evalyshop.models.image;

import com.google.gson.annotations.SerializedName;

public class Resize{

	@SerializedName("fit")
	private String fit;

	@SerializedName("width")
	private Object width;

	@SerializedName("height")
	private Object height;

	public void setFit(String fit){
		this.fit = fit;
	}

	public String getFit(){
		return fit;
	}


	public Object getWidth() {
		return width;
	}

	public void setWidth(Object width) {
		this.width = width;
	}

	public Object getHeight() {
		return height;
	}

	public void setHeight(Object height) {
		this.height = height;
	}

	@Override
 	public String toString(){
		return 
			"Resize{" + 
			"fit = '" + fit + '\'' + 
			",width = '" + width + '\'' + 
			",height = '" + height + '\'' + 
			"}";
		}
}