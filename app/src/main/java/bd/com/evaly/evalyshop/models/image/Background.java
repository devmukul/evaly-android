package bd.com.evaly.evalyshop.models.image;

import com.google.gson.annotations.SerializedName;

public class Background{

	@SerializedName("r")
	private int R;

	@SerializedName("g")
	private int G;

	@SerializedName("b")
	private int B;


	@SerializedName("alpha")
	private int alpha;

	public void setR(int R){
		this.R = R;
	}

	public int getR(){
		return R;
	}

	public void setB(int B){
		this.B = B;
	}

	public int getB(){
		return B;
	}

	public void setG(int G){
		this.G = G;
	}

	public int getG(){
		return G;
	}

	public void setAlpha(int alpha){
		this.alpha = alpha;
	}

	public int getAlpha(){
		return alpha;
	}

	@Override
 	public String toString(){
		return 
			"Background{" + 
			"r = '" + R + '\'' + 
			",b = '" + B + '\'' + 
			",g = '" + G + '\'' + 
			",alpha = '" + alpha + '\'' + 
			"}";
		}
}