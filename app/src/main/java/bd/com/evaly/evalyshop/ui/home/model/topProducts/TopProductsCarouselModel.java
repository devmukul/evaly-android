package bd.com.evaly.evalyshop.ui.home.model.topProducts;

import android.view.ViewGroup;

public class TopProductsCarouselModel extends TopProductsCarouselModel_ {

    @Override
    protected TopProductsCarousel buildView(ViewGroup parent) {
        return new TopProductsCarousel(parent.getContext());
    }
}
