package bd.com.evaly.evalyshop.models.image;

import com.google.gson.annotations.SerializedName;

public class Edits{


	@SerializedName("resize")
	private Resize resize;

	@SerializedName("background")
	private Background background;

	@SerializedName("flatten")
	private boolean flatten;

	@SerializedName("jpeg")
	private Jpeg jpeg;

	public void setFlatten(boolean flatten){
		this.flatten = flatten;
	}

	public boolean isFlatten(){
		return flatten;
	}

	public void setBackground(Background background){
		this.background = background;
	}

	public Background getBackground(){
		return background;
	}

	public void setResize(Resize resize){
		this.resize = resize;
	}

	public Resize getResize(){
		return resize;
	}

	public void setJpeg(Jpeg jpeg){
		this.jpeg = jpeg;
	}

	public Jpeg getJpeg(){
		return jpeg;
	}

	@Override
 	public String toString(){
		return 
			"Edits{" + 
			"flatten = '" + flatten + '\'' + 
			",background = '" + background + '\'' + 
			",resize = '" + resize + '\'' + 
			",jpeg = '" + jpeg + '\'' + 
			"}";
		}
}