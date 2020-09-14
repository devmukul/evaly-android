package bd.com.evaly.evalyshop.ui.campaign.model;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.airbnb.epoxy.Carousel;
import com.airbnb.epoxy.ModelView;

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
public class CampaignSliderCarouselModel extends Carousel {

    public CampaignSliderCarouselModel(Context context) {
        super(context);
        setNestedScrollingEnabled(true);
    }
}
