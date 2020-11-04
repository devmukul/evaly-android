package bd.com.evaly.evalyshop.util.reviewratings;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;

import static bd.com.evaly.evalyshop.util.reviewratings.Utils.getRoundedBarDrawable;
import static bd.com.evaly.evalyshop.util.reviewratings.Utils.getRoundedBarGradientDrawable;

public class RatingReviews extends FrameLayout {
    private Context mCtx;

    private int mBarSpaces;
    private int mBarTextColor;
    private int mBarDimension;
    private int mBarTextSize;
    private int mBarColor;
    private int mBarMaxValue;
    private int mStyle;
    private int mNumOfBars;
    private boolean isRoundCorner;

    private boolean isBarAdded = false;
    private boolean isShowLabel = true;
    private boolean isShowRaters = true;
    private boolean isShowAnimation = true;

    private LinearLayout mLinearParentLayout;

    private OnBarClickListener onBarClickListener;

    private List<Bar> mBars = new ArrayList<>();

    public RatingReviews(Context mCtx) {
        super(mCtx);
        initLayout();
    }

    public RatingReviews(Context mCtx, AttributeSet attrs) {
        super(mCtx, attrs);
        this.mCtx = mCtx;

        TypedArray a = mCtx.obtainStyledAttributes(attrs, R.styleable.RatingReviews, 0, 0);

        mStyle = a.getInt(R.styleable.RatingReviews_style, 1);
        mBarDimension = a.getDimensionPixelSize(R.styleable.RatingReviews_width,
                (int) Utils.convertDpToPixel(20, mCtx));
        mBarColor = a.getColor(R.styleable.RatingReviews_color, Utils.DEFAULT_BAR_COLOR);
        mBarTextSize = (int) Utils.convertPixelsToDp(
                a.getDimensionPixelSize(R.styleable.RatingReviews_text_size,
                        (int) Utils.convertDpToPixel(15, mCtx)), mCtx);
        mBarTextColor = a.getColor(R.styleable.RatingReviews_text_color, Utils.DEFAULT_BAR_TEXT_COLOR);
        mBarMaxValue = a.getInt(R.styleable.RatingReviews_max_value, 0);
        mBarSpaces = a.getDimensionPixelSize(R.styleable.RatingReviews_spaces,
                (int) Utils.convertDpToPixel(Utils.DEFAULT_BAR_SPACE, mCtx));
        isRoundCorner = a.getBoolean(R.styleable.RatingReviews_rounded, false);

        isShowLabel = a.getBoolean(R.styleable.RatingReviews_show_label, true);
        isShowRaters = a.getBoolean(R.styleable.RatingReviews_show_raters, true);
        isShowAnimation = a.getBoolean(R.styleable.RatingReviews_animation, true);

        this.mNumOfBars = 5;

        a.recycle();
        initLayout();
    }

    private void initLayout() {
        mLinearParentLayout = new LinearLayout(mCtx);
        mLinearParentLayout.setOrientation(LinearLayout.VERTICAL);
        mLinearParentLayout.setLayoutParams(
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mLinearParentLayout.setGravity(Gravity.START | Gravity.TOP); // Gravity.LEFT | Gravity.START
        if (isShowAnimation) {
            try {


                mLinearParentLayout.setLayoutTransition(new LayoutTransition());

            } catch (Exception e) {
                Log.e("ozii", e.toString());
            }
        }
        this.addView(mLinearParentLayout);
    }


    public void setMaxBarValue(int mBarMaxValue) {
        this.mBarMaxValue = mBarMaxValue; // 20+ scales much nicer but maybe should change soon.
        clearAll();
    }


    public void createRatingBars(int maxBarValue, String labels[], int colorsArr[], int raters[]) {

        setMaxBarValue(maxBarValue);

        for (int i = 0; i < mNumOfBars; i++) {
            Bar bar = new Bar();
            bar.setRaters(raters[i]);
            bar.setColor(colorsArr[i]);
            bar.setStarLabel(labels[i]);
            addBar(bar);
        }
    }


    public void createRatingBars(int maxBarValue, String labels[], Pair colors[], int raters[]) {

        setMaxBarValue(maxBarValue);

        for (int i = 0; i < mNumOfBars; i++) {
            Bar bar = new Bar();
            bar.setRaters(raters[i]);
            bar.setStartColor((int) colors[i].first);
            bar.setEndColor((int) colors[i].second);
            bar.setStarLabel(labels[i]);
            addBar(bar);
        }
    }


    public void createRatingBars(int maxBarValue, String labels[], int color, int raters[]) {

        setMaxBarValue(maxBarValue);

        for (int i = 0; i < mNumOfBars; i++) {
            Bar bar = new Bar();
            bar.setRaters(raters[i]);
            bar.setColor(color);
            bar.setStarLabel(labels[i]);
            addBar(bar);
        }
    }


    private void createBar(int dimension, final Bar bar) {
        if (dimension == 0 || mBarMaxValue == 0) {
            return;
        }

        int styleLayout;

        switch (mStyle) {
            case 1:
                styleLayout = R.layout.bar;
                break;
            case 2:
                styleLayout = R.layout.bar_two;
                break;
            default:
                styleLayout = R.layout.bar;
                break;
        }

        View view = LayoutInflater.from(mCtx).inflate(styleLayout, mLinearParentLayout, false);
        draw(dimension, null, bar, view);
    }


    private void draw(int dimension, Bar initialBar, final Bar bar, final View view) {
        final int bgColor = bar.getColor() != 0 ? bar.getColor() : mBarColor;

        view.post(new Runnable() {
            @Override
            public void run() {
                int radius = view.getHeight() / 2; //getting height inside post method to ensure view has drawn

                if (bar.isGradientBar()) {
                    if (bar.getStartColor() == 0 || bar.getEndColor() == 0) {
                        throw new RuntimeException("Gradient colors were not provided.");
                    }
                    if (isRoundCorner) {
                        view.findViewById(R.id.linear_bar).setBackground(getRoundedBarGradientDrawable(bar.getStartColor(), bar.getEndColor(), radius));
                    } else {
                        GradientDrawable gradientDrawable = new GradientDrawable(
                                GradientDrawable.Orientation.LEFT_RIGHT,
                                new int[]{bar.getStartColor(), bar.getEndColor()}
                        );
                        view.findViewById(R.id.linear_bar).setBackground(gradientDrawable);
                    }


                } else {
                    if (isRoundCorner) {

                        view.findViewById(R.id.linear_bar_bg).setBackground(getRoundedBarDrawable(ContextCompat.getColor(getContext(), R.color.f1f3f4), radius));
                        view.findViewById(R.id.linear_bar).setBackground(getRoundedBarDrawable(bgColor, radius));


                    } else {
                        view.findViewById(R.id.linear_bar_bg).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.f1f3f4));
                        view.findViewById(R.id.linear_bar).setBackgroundColor(bgColor);
                    }
                }
            }
        });


        int dimensionBar = dimension * bar.getRaters() / mBarMaxValue;

        MarginLayoutParams layoutParamsBar = (MarginLayoutParams) view.getLayoutParams();

        if (isShowLabel) {
            TextView textView = view.findViewById(R.id.text_view_bar_label);

            if (bar.getStarLabel() != null) {
                textView.setText(String.format(Locale.getDefault(), "%s", bar.getStarLabel()));
            }

            textView.setTextSize(mBarTextSize);
            textView.setTextColor(mBarTextColor);
        } else {
            view.findViewById(R.id.text_view_bar_label).setVisibility(GONE);
        }

        if (isShowRaters) {
            TextView raters = view.findViewById(R.id.text_view_raters);

            if (bar.getStarLabel() != null) {
                raters.setText(String.format(Locale.getDefault(), "%s", bar.getRaters()));
            }
        }

        final LinearLayout linearLayoutBar = view.findViewById(R.id.linear_bar);
        ValueAnimator anim = ValueAnimator.ofInt(
                initialBar == null ? 0 : dimension * initialBar.getRaters() / mBarMaxValue, dimensionBar);

        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams layoutParams = linearLayoutBar.getLayoutParams();
            layoutParams.width = (Integer) valueAnimator.getAnimatedValue();
            linearLayoutBar.setLayoutParams(layoutParams);
        });

        if (isShowAnimation) {
            anim.setDuration(500);
        } else {
            anim.setDuration(0);
        }
        anim.start();

        view.setOnClickListener(view1 -> {
            if (onBarClickListener != null) {
                onBarClickListener.onBarClick(bar);
            }
        });

        view.setTag(bar);

        view.getLayoutParams().height = mBarDimension;

        if (initialBar == null) {
            if (isBarAdded) {
                layoutParamsBar.topMargin = mBarSpaces;
            }
            mLinearParentLayout.addView(view);
        }

        isBarAdded = true;
    }


    @SuppressWarnings("deprecation")
    private void getDimension(final View view,
                              final DimensionReceivedCallback listener) {
        view.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        listener.onDimensionReceived(view.getWidth());
                    }
                });
    }

    public void setOnBarClickListener(OnBarClickListener onBarClickListener) {
        this.onBarClickListener = onBarClickListener;
    }

    public void addBar(final Bar bar) {
        addBar(mBars.size(), bar);
    }

    public void addBar(final int position, final Bar bar) {
        if (position > mNumOfBars) {
            return;
        }
        mBars.add(bar);
        if (bar != null) {
            if (mLinearParentLayout.getHeight() == 0) {
                getDimension(mLinearParentLayout, dimension -> createBar(dimension, bar));
            } else {
                createBar(mLinearParentLayout.getWidth(), bar);
            }
        }
    }
    
    public void clearAll() {
        mBars.clear();
        mLinearParentLayout.removeAllViews();
    }

    private interface DimensionReceivedCallback {
        void onDimensionReceived(int dimension);
    }

    public interface OnBarClickListener {
        void onBarClick(Bar bar);
    }
}