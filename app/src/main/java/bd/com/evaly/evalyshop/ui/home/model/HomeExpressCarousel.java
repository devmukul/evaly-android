package bd.com.evaly.evalyshop.ui.home.model;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

@ModelView(saveViewState = true, defaultLayout = 0, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
public class HomeExpressCarousel extends Carousel {

    public HomeExpressCarousel(Context context) {
        super(context);
        setNestedScrollingEnabled(false);
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
        return new GridLayoutManager(getContext(), 4,
                GridLayoutManager.VERTICAL, false);
    }

}
