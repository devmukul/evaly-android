package bd.com.evaly.evalyshop.reviewratings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.Pair;

import java.util.HashMap;
import java.util.Map;


class Utils {
    static int DEFAULT_BAR_COLOR = Color.parseColor("#333333");
    static int DEFAULT_BAR_TEXT_COLOR = Color.parseColor("#333333");
    static int DEFAULT_BAR_SPACE = 5;


    static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    static Drawable getRoundedBarDrawable(int bgColor, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadii(new float[]{
                radius, radius, radius, radius, radius, radius, radius, radius
        });
        drawable.setColor(bgColor);
        return drawable;
    }



    static GradientDrawable getRoundedBarGradientDrawable(int startColor, int endColor, int radius) {

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {startColor, endColor});
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadii(new float[]{
                radius, radius, radius, radius, radius, radius, radius, radius
        });
        return gradientDrawable;
    }
}