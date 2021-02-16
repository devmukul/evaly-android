package bd.com.evaly.evalyshop.ui.home.model;

import android.view.ViewGroup;

public class HomeDefaultCarouselModel extends HomeDefaultCarouselModel_ {
    @Override
    protected HomeDefaultCarousel buildView(ViewGroup parent) {
        return new HomeDefaultCarousel(parent.getContext());
    }
}
