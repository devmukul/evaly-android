package bd.com.evaly.evalyshop.ui.campaign.campaignDetails.controller;

import androidx.navigation.NavController;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.campaign.CampaignParentModel;
import bd.com.evaly.evalyshop.models.campaign.brand.CampaignBrandResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.campaign.shop.CampaignShopResponse;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignBrandModel_;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignProductModel_;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignShopModel_;

public class CampaignCategoryController extends EpoxyController {

    private List<CampaignParentModel> list = new ArrayList<>();
    private NavController navController;

    @Override
    protected void buildModels() {

        for (CampaignParentModel item : list) {
            if (item instanceof CampaignProductResponse)
                new CampaignProductModel_()
                        .id(((CampaignProductResponse) item).getSlug())
                        .model((CampaignProductResponse) item)
                        .addTo(this);
            else if (item instanceof CampaignBrandResponse)
                new CampaignBrandModel_()
                        .id(((CampaignBrandResponse) item).getSlug())
                        .model((CampaignBrandResponse) item)
                        .addTo(this);
            else if (item instanceof CampaignShopResponse)
                new CampaignShopModel_()
                        .id(((CampaignShopResponse) item).getSlug())
                        .model((CampaignShopResponse) item)
                        .addTo(this);
        }

    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public void setList(List<CampaignParentModel> list) {
        this.list = list;
    }
}
