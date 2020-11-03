package bd.com.evaly.evalyshop.ui.home.model;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

import bd.com.evaly.evalyshop.util.Utils;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT, baseModelClass = EpoxyModelStaggeredGrid.class)
public class HomeRsCarousel extends Carousel {

    public HomeRsCarousel(Context context) {
        super(context);
        setNestedScrollingEnabled(false);
        setMinimumHeight((int) Utils.convertDpToPixel(175, getContext()));

//        setPadding(new Carousel.Padding(
//                (int) Utils.convertDpToPixel(15, getContext()),
//                (int) Utils.convertDpToPixel(12, getContext()),
//                (int) Utils.convertDpToPixel(10, getContext()),
//                (int) Utils.convertDpToPixel(10, getContext()),
//                (int) Utils.convertDpToPixel(10, getContext())));
//
//        if (getRootView().getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
//            ((StaggeredGridLayoutManager.LayoutParams) getRootView().getLayoutParams()).setFullSpan(true);
//        } else {
//            StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//            );
//            params.setFullSpan(true);
//            getRootView().setLayoutParams(params);
//        }
    }

    @Nullable
    @Override
    protected SnapHelperFactory getSnapHelperFactory() {
        return null;
    }

    @Nullable
    @Override
    public LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getContext(), HORIZONTAL, false);
    }
}
