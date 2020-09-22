package bd.com.evaly.evalyshop.ui.campaign.campaignDetails.controller;

import androidx.navigation.NavController;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignProductModel_;

public class CampaignCategoryController extends EpoxyController {

    private List<CampaignProductResponse> productList = new ArrayList<>();
    private NavController navController;

    @Override
    protected void buildModels() {

        for (CampaignProductResponse item : productList) {
            new CampaignProductModel_()
                    .id(item.getSlug())
                    .model(item)
                    .addTo(this);
        }

    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public void setProductList(List<CampaignProductResponse> productList) {
        this.productList = productList;
    }
}
