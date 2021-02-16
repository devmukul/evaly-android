package bd.com.evaly.evalyshop.ui.home.model.cyclone;

import android.view.ViewGroup;

public class CycloneCarouselModel extends CycloneCarouselModel_ {

    @Override
    protected CycloneCarousel buildView(ViewGroup parent) {
        return new CycloneCarousel(parent.getContext());
    }
}
