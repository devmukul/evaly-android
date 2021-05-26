package bd.com.evaly.evalyshop.models.points;

import com.google.gson.annotations.SerializedName;

public class PointsResponse{

	@SerializedName("level")
	private String level;

	@SerializedName("platform")
	private String platform;

	@SerializedName("points")
	private int points;

	public void setLevel(String level){
		this.level = level;
	}

	public String getLevel(){
		return level;
	}

	public void setPlatform(String platform){
		this.platform = platform;
	}

	public String getPlatform(){
		return platform;
	}

	public void setPoints(int points){
		this.points = points;
	}

	public int getPoints(){
		return points;
	}

	@Override
 	public String toString(){
		return 
			"PointsResponse{" + 
			"level = '" + level + '\'' + 
			",platform = '" + platform + '\'' + 
			",points = '" + points + '\'' + 
			"}";
		}
}