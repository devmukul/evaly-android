package bd.com.evaly.evalyshop.ui.home.model.slider;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.views.FixedLinearLayoutManager;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.WRAP_WIDTH_WRAP_HEIGHT)
public class HomeSliderCarousel extends Carousel {

    public HomeSliderCarousel(Context context) {
        super(context);
        setNestedScrollingEnabled(false);

        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setFullSpan(true);
        setLayoutParams(params);
        setBackgroundColor(getResources().getColor(R.color.white));
    }

    @Nullable
    @Override
    public LayoutManager createLayoutManager() {
        return new FixedLinearLayoutManager(getContext(), HORIZONTAL, false);
    }

}
