package bd.com.evaly.evalyshop.ui.home.model.slider;

import android.view.ViewGroup;

public class HomeSliderCarouselModel extends HomeSliderCarouselModel_ {

    private CirclePagerIndicatorDecoration decoration = new CirclePagerIndicatorDecoration();
    // private IndefinitePagerIndicator indicator;

    @Override
    protected HomeSliderCarousel buildView(ViewGroup parent) {
        // indicator = new IndefinitePagerIndicator(parent.getContext());
        return new HomeSliderCarousel(parent.getContext());
    }

    @Override
    public void bind(HomeSliderCarousel carousel) {
        super.bind(carousel);
        //  indicator.attachToRecyclerView(carousel);
        carousel.addItemDecoration(decoration);
    }

    @Override
    public void unbind(HomeSliderCarousel carousel) {
        super.unbind(carousel);
        carousel.removeItemDecoration(decoration);
    }

}
