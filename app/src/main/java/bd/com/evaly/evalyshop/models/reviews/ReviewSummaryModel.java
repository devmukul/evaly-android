package bd.com.evaly.evalyshop.models.reviews;

import com.google.gson.annotations.SerializedName;

public class ReviewSummaryModel {

    @SerializedName("total_ratings")
    private int totalRatings;

    @SerializedName("star_5")
    private int star5;

    @SerializedName("avg_rating")
    private double avgRating;

    @SerializedName("star_2")
    private int star2;

    @SerializedName("star_1")
    private int star1;

    @SerializedName("star_4")
    private int star4;

    @SerializedName("star_3")
    private int star3;

    public int getTotalRatings() {
        return totalRatings;
    }

    public int getStar5() {
        return star5;
    }

    public double getAvgRating() {
        if (avgRating == 0)
            return 0.0;
        else
            return avgRating;
    }

    public int getStar2() {
        return star2;
    }

    public int getStar1() {
        return star1;
    }

    public int getStar4() {
        return star4;
    }

    public int getStar3() {
        return star3;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public void setStar5(int star5) {
        this.star5 = star5;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public void setStar2(int star2) {
        this.star2 = star2;
    }

    public void setStar1(int star1) {
        this.star1 = star1;
    }

    public void setStar4(int star4) {
        this.star4 = star4;
    }

    public void setStar3(int star3) {
        this.star3 = star3;
    }
}