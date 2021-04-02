package bd.com.evaly.evalyshop.ui.home.model.slider;

import android.view.ViewGroup;

public class HomeSliderCarouselModel extends HomeSliderCarouselModel_ {

    private CirclePagerIndicatorDecoration decoration = new CirclePagerIndicatorDecoration();

    @Override
    protected HomeSliderCarousel buildView(ViewGroup parent) {
        return new HomeSliderCarousel(parent.getContext());
    }

    @Override
    public void bind(HomeSliderCarousel carousel) {
        super.bind(carousel);
        carousel.addItemDecoration(decoration);
    }

    @Override
    public void unbind(HomeSliderCarousel carousel) {
        super.unbind(carousel);
        carousel.removeItemDecoration(decoration);
    }

}
