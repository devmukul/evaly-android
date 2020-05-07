package bd.com.evaly.evalyshop.ui.home.model;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
public class HomeCarouselModel extends Carousel {

    public HomeCarouselModel(Context context) {
        super(context);
        setNestedScrollingEnabled(false);
    }



    @Nullable
    @Override
    protected SnapHelperFactory getSnapHelperFactory() {
        return null;
    }

    @Nullable
    @Override
    public LayoutManager createLayoutManager() {
        return new GridLayoutManager(getContext(), 2,
                GridLayoutManager.VERTICAL, false);
    }
}
