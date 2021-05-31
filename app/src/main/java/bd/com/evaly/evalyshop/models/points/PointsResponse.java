package bd.com.evaly.evalyshop.models.points;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PointsResponse {

    @SerializedName("level")
    private String level;

    @SerializedName("platform")
    private String platform;

    @SerializedName("points")
    private double points;

    @SerializedName("interval")
    private List<Integer> interval;

    public List<Integer> getInterval() {
        if (interval == null)
            return new ArrayList<>();
        return interval;
    }

    public void setInterval(List<Integer> interval) {
        this.interval = interval;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getPoints() {
        return (int) points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return
                "PointsResponse{" +
                        "level = '" + level + '\'' +
                        ",platform = '" + platform + '\'' +
                        ",points = '" + points + '\'' +
                        "}";
    }
}