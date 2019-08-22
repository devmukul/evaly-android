package bd.com.evaly.evalyshop.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ChildWrappingAdjustableViewPager extends ViewPager {
    List<Integer> childHeights = new ArrayList<>(getChildCount());
    int minHeight = 0;
    int currentPos = 0;

    private View mCurrentView;

    public ChildWrappingAdjustableViewPager(@NonNull Context context) {
        super(context);
        setOnPageChangeListener();
    }

    public ChildWrappingAdjustableViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        obtainMinHeightAttribute(context, attrs);
        setOnPageChangeListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        if (getChildCount() <= 0) {

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        childHeights.clear();

        //calculate child views
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h < minHeight) {
                h = minHeight;
            }
            childHeights.add(i, h);
        }

        if (childHeights.size() - 1 >= currentPos) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeights.get(currentPos), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }



    public void measureCurrentView(View currentView) {
        mCurrentView = currentView;
        requestLayout();
    }




    private void obtainMinHeightAttribute(@NonNull Context context, @Nullable AttributeSet attrs) {
        int[] heightAttr = new int[]{android.R.attr.minHeight};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, heightAttr);
        minHeight = typedArray.getDimensionPixelOffset(0, -666);
        typedArray.recycle();
    }

    private void setOnPageChangeListener() {

        try {


            this.addOnPageChangeListener(new SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {

                    if (position<=childHeights.size()) {

                        currentPos = position;

                        ViewGroup.LayoutParams layoutParams = ChildWrappingAdjustableViewPager.this.getLayoutParams();
                        layoutParams.height = childHeights.get(position);
                        ChildWrappingAdjustableViewPager.this.setLayoutParams(layoutParams);
                        ChildWrappingAdjustableViewPager.this.invalidate();
                    }
                }
            });

        } catch (Exception e){

        }
    }
}