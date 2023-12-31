package bd.com.evaly.evalyshop.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import bd.com.evaly.evalyshop.listener.ObjectAtPositionInterface;

public class WrapContentHeightViewPager extends ViewPager {

    private static final String TAG = WrapContentHeightViewPager.class.getSimpleName();
    private int height = 0;
    private int decorHeight = 0;
    private int widthMeasuredSpec;

    private boolean animateHeight;
    private int rightHeight;
    private int leftHeight;
    private int scrollingPosition = -1;

    private boolean isFirst = true;

    public WrapContentHeightViewPager(Context context) {
        super(context);
        init();
    }

    public WrapContentHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        addOnPageChangeListener(new OnPageChangeListener() {

            public int state;

            @Override
            public void onPageScrolled(int position, float offset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (state == SCROLL_STATE_IDLE) {
                    height = 0; // measure the selected page in-case it's a change without scrolling
                    Log.d(TAG, "onPageSelected:" + position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                this.state = state;
            }
        });
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (!(adapter instanceof ObjectAtPositionInterface)) {
            throw new IllegalArgumentException("WrapContentViewPage requires that PagerAdapter will implement ObjectAtPositionInterface");
        }
        height = 0; // so we measure the new content in onMeasure
        super.setAdapter(adapter);
    }

    /**
     * Allows to redraw the view size to wrap the content of the bigger child.
     *
     * @param widthMeasureSpec  with measured
     * @param heightMeasureSpec height measured
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        widthMeasuredSpec = widthMeasureSpec;
        int mode = MeasureSpec.getMode(heightMeasureSpec);


        int pos = getCurrentItem();


//        if (getAdapter() != null &&  getAdapter().getCount() == 2)) {
//            pos = 0;
//        }

        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {

            if (height == 0 || pos == 0) {

                decorHeight = 0;

                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    if (lp != null && lp.isDecor) {
                        int vgrav = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;
                        boolean consumeVertical = vgrav == Gravity.TOP || vgrav == Gravity.BOTTOM;
                        if (consumeVertical) {
                            decorHeight += child.getMeasuredHeight();
                        }
                    }
                }

                int position = getCurrentItem();
                View child = getViewAtPosition(position);
                if (child != null) {
                    height = measureViewHeight(child);
                }

            }

            int totalHeight = height + decorHeight + getPaddingBottom() + getPaddingTop();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public void onPageScrolled(int position, float offset, int positionOffsetPixels) {
        super.onPageScrolled(position, offset, positionOffsetPixels);

        // cache scrolled view heights
        if (scrollingPosition != position) {
            scrollingPosition = position;
            // scrolled position is always the left scrolled page
            View leftView = getViewAtPosition(position);
            View rightView = getViewAtPosition(position + 1);
            if (leftView != null && rightView != null) {
                leftHeight = measureViewHeight(leftView);
                rightHeight = measureViewHeight(rightView);
                animateHeight = true;
                Log.d(TAG, "onPageScrolled heights left:" + leftHeight + " right:" + rightHeight);
            } else {
                animateHeight = false;
            }
        }
        if (animateHeight) {
            int newHeight = (int) (leftHeight * (1 - offset) + rightHeight * (offset));
            if (height != newHeight) {
                Log.d(TAG, "onPageScrolled height change:" + newHeight);
                height = newHeight;
                requestLayout();
                invalidate();
            }
        }
    }


    public int measureViewHeight(View view) {


        view.measure(getChildMeasureSpec(widthMeasuredSpec, getPaddingLeft() + getPaddingRight(), view.getLayoutParams().width), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }

    public void updateView(View view) {
        height = measureViewHeight(view);
        measure(widthMeasuredSpec, height);
        requestLayout();
        invalidate();
    }


    public void refreshHeight() {
        requestLayout();
        invalidate();
    }

    protected View getViewAtPosition(int position) {

        isFirst = false;

        if (getAdapter() != null) {
            Object objectAtPosition = ((ObjectAtPositionInterface) getAdapter()).getObjectAtPosition(position);
            if (objectAtPosition != null) {
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    if (child != null && getAdapter().isViewFromObject(child, objectAtPosition)) {
                        return child;
                    }
                }
            }
        }
        return null;
    }


}