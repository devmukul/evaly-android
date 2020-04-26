package bd.com.evaly.evalyshop.ui.home.model;

import android.content.Context;

import androidx.annotation.Nullable;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
public class HomeCarouselModel extends Carousel {

    public HomeCarouselModel(Context context) {
        super(context);
    }

    @Nullable
    @Override
    protected SnapHelperFactory getSnapHelperFactory() {
        return null;
    }

    @Nullable
    @Override
    public LayoutManager getLayoutManager() {
        return super.getLayoutManager();
    }
}
