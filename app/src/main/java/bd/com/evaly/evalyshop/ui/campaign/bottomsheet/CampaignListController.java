package bd.com.evaly.evalyshop.ui.campaign.bottomsheet;

import androidx.navigation.NavController;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignSubModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoItemModel_;

public class CampaignListController extends EpoxyController {

    private List<SubCampaignResponse> list = new ArrayList<>();
    private NavController navController;
    private boolean isLoading = true;
    private ClickListener clickListener;


    public interface ClickListener {
        void onClick(SubCampaignResponse model);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    @Override
    protected void buildModels() {
        for (SubCampaignResponse item : list)
            new CampaignSubModel_()
                    .id(item.getSlug())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> clickListener.onClick(model.model()))
                    .addTo(this);

        new NoItemModel_()
                .id("no_product_model")
                .text("No campaign found")
                .image(R.drawable.ic_discount_tag)
                .width(80)
                .addIf(list.size() == 0 && !isLoading, this);

        new LoadingModel_()
                .id("bottom_loading_model")
                .addIf(isLoading, this);
    }

    public void setList(List<SubCampaignResponse> list) {
        this.list = list;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
