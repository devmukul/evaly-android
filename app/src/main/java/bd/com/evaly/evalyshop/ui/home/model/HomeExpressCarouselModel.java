package bd.com.evaly.evalyshop.ui.home.model;

import android.view.ViewGroup;

public class HomeExpressCarouselModel extends HomeExpressCarouselModel_ {

    @Override
    protected HomeExpressCarousel buildView(ViewGroup parent) {
        return new HomeExpressCarousel(parent.getContext());
    }
}
