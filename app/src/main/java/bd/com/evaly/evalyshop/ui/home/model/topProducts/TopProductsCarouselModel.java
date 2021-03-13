package bd.com.evaly.evalyshop.ui.home.model.topProducts;

import android.view.ViewGroup;

import bd.com.evaly.evalyshop.ui.home.model.HomeRsCarousel;
import bd.com.evaly.evalyshop.ui.home.model.HomeRsCarouselModel_;

public class TopProductsCarouselModel extends TopProductsCarouselModel_ {

    @Override
    protected TopProductsCarousel buildView(ViewGroup parent) {
        return new TopProductsCarousel(parent.getContext());
    }
}
