package bd.com.evaly.evalyshop.ui.home.model.topProducts;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

import bd.com.evaly.evalyshop.ui.home.model.EpoxyModelStaggeredGrid;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
public class TopProductsCarousel extends Carousel {

    public TopProductsCarousel(Context context) {
        super(context);
        setNestedScrollingEnabled(false);
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
        return new LinearLayoutManager(getContext(), HORIZONTAL, false);
    }
}
