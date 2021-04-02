package bd.com.evaly.evalyshop.ui.home.model.slider;

import android.view.ViewGroup;

public class HomeSliderCarouselModel extends HomeSliderCarouselModel_ {

    private CirclePagerIndicatorDecoration decoration = new CirclePagerIndicatorDecoration();
    // private ScrollingPagerIndicator indicator;

    @Override
    protected HomeSliderCarousel buildView(ViewGroup parent) {
//        indicator = new ScrollingPagerIndicator(parent.getContext());
//        indicator.setOrientation(ScrollingPagerIndicator.SCROLL_AXIS_HORIZONTAL);
//        indicator.setSelectedDotColor(Color.BLACK);
//        indicator.setDotColor(Color.WHITE);
        return new HomeSliderCarousel(parent.getContext());
    }

    @Override
    public void bind(HomeSliderCarousel carousel) {
        super.bind(carousel);
        // indicator.attachToRecyclerView(carousel);
        carousel.addItemDecoration(decoration);
    }

    @Override
    public void unbind(HomeSliderCarousel carousel) {
        super.unbind(carousel);
        carousel.removeItemDecoration(decoration);
    }

}
