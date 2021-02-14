package bd.com.evaly.evalyshop.ui.home.model.cyclone;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.views.FixedLinearLayoutManager;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
public class CycloneCarousel extends Carousel {

    public CycloneCarousel(Context context) {
        super(context);
        setNestedScrollingEnabled(false);
        if (getContext() != null)
            setBackground(getContext().getResources().getDrawable(R.drawable.bg_cyclone_black));
        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setFullSpan(true);
        setLayoutParams(params);
    }

    @Nullable
    @Override
    protected SnapHelperFactory getSnapHelperFactory() {
        return null;
    }

    @Nullable
    @Override
    public LayoutManager createLayoutManager() {
        return new FixedLinearLayoutManager(getContext(), HORIZONTAL, false);
    }
}
