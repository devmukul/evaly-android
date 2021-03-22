package bd.com.evaly.evalyshop.ui.home.model;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.views.FixedLinearLayoutManager;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
public class HomeRsCarousel extends Carousel {

    public HomeRsCarousel(Context context) {
        super(context);
        setNestedScrollingEnabled(false);
        setMinimumHeight((int) Utils.convertDpToPixel(160, getContext()));
        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setFullSpan(true);
        setLayoutParams(params);
    }

    @Nullable
    @Override
    protected SnapHelperFactory getSnapHelperFactory() {
        return null;
    }

    @Nullable
    @Override
    public LayoutManager createLayoutManager() {
        return new FixedLinearLayoutManager(getContext(), HORIZONTAL, false);
    }
}
