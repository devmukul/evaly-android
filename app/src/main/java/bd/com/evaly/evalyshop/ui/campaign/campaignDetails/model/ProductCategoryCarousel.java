package bd.com.evaly.evalyshop.ui.campaign.campaignDetails.model;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

import bd.com.evaly.evalyshop.ui.home.model.EpoxyModelStaggeredGrid;
import bd.com.evaly.evalyshop.util.Utils;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT, baseModelClass = EpoxyModelStaggeredGrid.class)
public class ProductCategoryCarousel extends Carousel {

    public ProductCategoryCarousel(Context context) {
        super(context);
        setNestedScrollingEnabled(false);
        setMinimumHeight((int) Utils.convertDpToPixel(175, getContext()));
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
