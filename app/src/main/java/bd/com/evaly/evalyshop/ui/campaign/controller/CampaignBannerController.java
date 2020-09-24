package bd.com.evaly.evalyshop.ui.campaign.controller;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyController;
import com.airbnb.epoxy.OnModelClickListener;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignSliderModel_;

public class CampaignBannerController extends EpoxyController {

    private List<CampaignCategoryResponse> items = new ArrayList<>();
    private NavController navController;

    @Override
    protected void buildModels() {
        for (CampaignCategoryResponse item : items) {
            new CampaignSliderModel_()
                    .id(item.getName())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("model", model.model());
                        navController.navigate(R.id.campaignDetails, bundle);
                    })
                    .addIf(item.getBannerImage() != null, this);
        }
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public void addData(List<CampaignCategoryResponse> list) {
        Log.d("hmt size", "size " + list.size());
        this.items = list;
    }
}