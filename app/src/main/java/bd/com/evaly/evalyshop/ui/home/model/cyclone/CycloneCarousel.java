package bd.com.evaly.evalyshop.ui.home.model.cyclone;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.home.model.EpoxyModelStaggeredGrid;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT, baseModelClass = EpoxyModelStaggeredGrid.class)
public class CycloneCarousel extends Carousel {

    public CycloneCarousel(Context context) {
        super(context);
        setNestedScrollingEnabled(false);
        if (getContext() != null)
            setBackground(getContext().getResources().getDrawable(R.drawable.bg_cyclone_black));
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
