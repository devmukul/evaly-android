package bd.com.evaly.evalyshop.ui.shop.models;

import android.content.Context;

import androidx.annotation.Nullable;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
public class ShopCategoryCarousel extends Carousel {

    public ShopCategoryCarousel(Context context) {
        super(context);
    }

    @Nullable
    @Override
    protected SnapHelperFactory getSnapHelperFactory() {
        return null;
    }
//
////    @Nullable
////    @Override
////    public LayoutManager createLayoutManager() {
////        return new Lin(getContext(), 1,
////                GridLayoutManager.HORIZONTAL, false);
////    }
}
