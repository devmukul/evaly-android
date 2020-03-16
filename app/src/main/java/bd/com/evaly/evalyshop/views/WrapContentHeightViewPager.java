package bd.com.evaly.evalyshop.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class WrapContentHeightViewPager extends ViewPager {

    private View mCurrentView;
    private Boolean mAnimStarted = false;

    public WrapContentHeightViewPager(Context context) {
        super(context);
    }

    /**
     * Constructor
     *
     * @param context the context
     * @param attrs   the attribute set
     */
    public WrapContentHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mCurrentView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int height = 0;
        mCurrentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int h = mCurrentView.getMeasuredHeight();
        if (h > height) height = h;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);


        if (heightMeasureSpec < 1400)
            heightMeasureSpec = 1400;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }




//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int mode = MeasureSpec.getMode(heightMeasureSpec);
//        // Unspecified means that the ViewPager is in a ScrollView WRAP_CONTENT.
//        // At Most means that the ViewPager is not in a ScrollView WRAP_CONTENT.
//        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
//            // super has to be called in the beginning so the child views can be initialized.
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            int height = 0;
//            for (int i = 0; i < getChildCount(); i++) {
//                View child = getChildAt(i);
//                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//                int h = child.getMeasuredHeight();
//                if (h > height) height = h;
//            }
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//        }
//        // super has to be called again so the new specs are treated as exact measurements
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }


    // with animation


//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        if (mCurrentView == null) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            return;
//        }
//
//        if(!mAnimStarted) {
//            int height = 0;
//        mCurrentView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//        int newHeight = mCurrentView.getMeasuredHeight();
//        if (newHeight > height) height = newHeight;
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//
//            if (getLayoutParams().height != 0 && heightMeasureSpec != newHeight && heightMeasureSpec > newHeight) {
//                final int targetHeight = height;
//                final int currentHeight = getLayoutParams().height;
//                final int heightChange = targetHeight - currentHeight;
//
//                Animation a = new Animation() {
//                    @Override
//                    protected void applyTransformation(float interpolatedTime, Transformation t) {
//                        if (interpolatedTime >= 1) {
//                            getLayoutParams().height = targetHeight;
//                        } else {
//                            int stepHeight = (int) (heightChange * interpolatedTime);
//                            getLayoutParams().height = currentHeight + stepHeight;
//                        }
//                        requestLayout();
//                    }
//
//                    @Override
//                    public boolean willChangeBounds() {
//                        return true;
//                    }
//                };
//
//                a.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                        mAnimStarted = true;
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        mAnimStarted = false;
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//                });
//
//                a.setDuration(300);
//                startAnimation(a);
//                mAnimStarted = true;
//            } else {
//                heightMeasureSpec = newHeight;
//            }
//        }
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//
//


    private int measureHeight(int measureSpec, View view) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            // set the height from the base view if available
            if (view != null) {
                result = view.getMeasuredHeight();
            }
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    public void measureCurrentView(View currentView) {
        mCurrentView = currentView;
        requestLayout();
    }

    public int measureFragment(View view) {
        if (view == null)
            return 0;

        view.measure(0, 0);
        return view.getMeasuredHeight();
    }

    @Override
    protected void onDetachedFromWindow() {
        mCurrentView = null;
        super.onDetachedFromWindow();
    }

    public void destroyView() {
        mCurrentView = null;
    }

}