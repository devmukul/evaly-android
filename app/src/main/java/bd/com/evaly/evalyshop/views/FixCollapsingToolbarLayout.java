package bd.com.evaly.evalyshop.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.lang.reflect.Field;
import java.util.Objects;

public class FixCollapsingToolbarLayout extends CollapsingToolbarLayout {


    public FixCollapsingToolbarLayout(Context context) {
        super(context);
    }

    public FixCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            Field fs = Objects.requireNonNull(this.getClass().getSuperclass()).getDeclaredField("lastInsets");
            fs.setAccessible(true);
            WindowInsetsCompat mLastInsets = (WindowInsetsCompat) fs.get(this);
            final int mode = MeasureSpec.getMode(heightMeasureSpec);
            int topInset = mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
            if (mode == MeasureSpec.UNSPECIFIED && topInset > 0) {
                // fix the bottom empty padding
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        getMeasuredHeight() - topInset, MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } catch (Exception e) {
            Log.e("Toolbar", "FixCollapsingToolbarLayout Error", e);
        }
    }
}
