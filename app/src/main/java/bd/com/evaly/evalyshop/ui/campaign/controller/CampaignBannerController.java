package bd.com.evaly.evalyshop.ui.campaign.controller;


import android.util.Log;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignSliderModel_;

public class CampaignBannerController extends EpoxyController {

    private List<CampaignCategoryResponse> items = new ArrayList<>();

    @Override
    protected void buildModels() {
        for (CampaignCategoryResponse item : items) {
            new CampaignSliderModel_()
                    .id(item.getName())
                    .model(item)
                    .addIf(item.getBannerImage() != null, this);
        }
    }

    public void addData(List<CampaignCategoryResponse> list) {
        Log.d("hmt size", "size " + list.size());
        this.items = list;
    }
}