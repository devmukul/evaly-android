package bd.com.evaly.evalyshop.ui.home.model;

import android.view.ViewGroup;

public class HomeRsCarouselModel extends HomeRsCarouselModel_ {

    @Override
    protected HomeRsCarousel buildView(ViewGroup parent) {
        return new HomeRsCarousel(parent.getContext());
    }
}
