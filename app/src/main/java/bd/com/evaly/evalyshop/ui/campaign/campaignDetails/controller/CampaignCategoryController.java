package bd.com.evaly.evalyshop.ui.campaign.campaignDetails.controller;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.campaign.CampaignParentModel;
import bd.com.evaly.evalyshop.models.campaign.brand.CampaignBrandResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.models.campaign.shop.CampaignShopResponse;
import bd.com.evaly.evalyshop.ui.campaign.campaignDetails.CampaignDetailsViewModel;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignBrandModel_;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignProductModel_;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignShopModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoItemModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;

public class CampaignCategoryController extends EpoxyController {

    private List<CampaignParentModel> list = new ArrayList<>();
    private NavController navController;
    private boolean isLoading = true;
    private CampaignDetailsViewModel viewModel;
    private AppCompatActivity activity;

    @Override
    protected void buildModels() {

        for (CampaignParentModel item : list) {
            if (item instanceof CampaignProductResponse)
                new CampaignProductModel_()
                        .id(((CampaignProductResponse) item).getSlug())
                        .model((CampaignProductResponse) item)
                        .clickListener((model, parentView, clickedView, position) -> {
                            CampaignProductResponse item1 = model.model();
                            Intent intent = new Intent(activity, ViewProductActivity.class);
                            intent.putExtra("product_slug", item1.getSlug());
                            intent.putExtra("product_name", item1.getName());
                            intent.putExtra("product_price", item1.getPrice());
                            if (item1.getShopSlug() != null)
                                intent.putExtra("shop_slug", item1.getShopSlug());
                            intent.putExtra("product_image", item1.getImage());
                            intent.putExtra("cashback_text", item1.getCashbackText());
                            activity.startActivity(intent);
                        })
                        .addTo(this);
            else if (item instanceof CampaignBrandResponse)
                new CampaignBrandModel_()
                        .id(((CampaignBrandResponse) item).getSlug())
                        .model((CampaignBrandResponse) item)
                        .clickListener((model, parentView, clickedView, position) -> {
                            Bundle bundle = new Bundle();
                            bundle.putInt("type", 1);
                            bundle.putString("shop_name", model.model().getName());
                            bundle.putString("logo_image", model.model().getImage());
                            bundle.putString("shop_slug", model.model().getShopSlug());
                            bundle.putString("category", "root");
                            bundle.putString("brand_slug", model.model().getSlug());
                            bundle.putString("campaign_slug", model.model().getCampaignSlug());
                            navController.navigate(R.id.shopFragment, bundle);
                        })
                        .addTo(this);
            else if (item instanceof CampaignShopResponse)
                new CampaignShopModel_()
                        .id(((CampaignShopResponse) item).getSlug())
                        .model((CampaignShopResponse) item)
                        .clickListener((model, parentView, clickedView, position) -> {
                            Bundle bundle = new Bundle();
                            bundle.putInt("type", 2);
                            bundle.putString("shop_name", model.model().getName());
                            bundle.putString("logo_image", model.model().getImage());
                            bundle.putString("shop_slug", model.model().getSlug());
                            bundle.putString("category", "root");
                            bundle.putString("campaign_slug", model.model().getCampaignSlug());
                            navController.navigate(R.id.shopFragment, bundle);
                        })
                        .addTo(this);
        }

        new NoItemModel_()
                .id("no_product_model")
                .text(getEmptyText())
                .image(R.drawable.ic_empty_product)
                .width(100)
                .addIf(list.size() == 0 && !isLoading, this);

        new LoadingModel_()
                .id("bottom_loading_model")
                .addIf(isLoading, this);
    }

    private String getEmptyText() {
        String type = viewModel.getType();
        if (type == null || type.equals("product"))
            return "No product found";
        else if (type.equals("shop"))
            return "No shop found";
        else if (type.equals("brand"))
            return "No brand found";
        else
            return "No product found";
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setViewModel(CampaignDetailsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public void setList(List<CampaignParentModel> list) {
        this.list = list;
    }
}
