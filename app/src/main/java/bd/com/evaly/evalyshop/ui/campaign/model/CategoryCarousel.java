package bd.com.evaly.evalyshop.ui.campaign.model;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
public class CategoryCarousel extends Carousel {

    public CategoryCarousel(Context context) {
        super(context);
        setNestedScrollingEnabled(true);
        addStatesFromChildren();
    }



    @Nullable
    @Override
    protected SnapHelperFactory getSnapHelperFactory() {
        return null;
    }

    @Nullable
    @Override
    public LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    }
}
