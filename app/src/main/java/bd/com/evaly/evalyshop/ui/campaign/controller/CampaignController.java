package bd.com.evaly.evalyshop.ui.campaign.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignButtonModel_;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignProductModel_;
import bd.com.evaly.evalyshop.ui.campaign.model.CategoryCarouselModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoItemModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;

public class CampaignController extends EpoxyController {

    @AutoModel
    CategoryCarouselModel_ buttonCarousel;
    private List<CampaignCategoryResponse> categoryList = new ArrayList<>();
    private List<CampaignProductResponse> productList = new ArrayList<>();
    private NavController navController;
    private boolean isLoading = true;
    private AppCompatActivity activity;

    public CampaignController(){
        setFilterDuplicates(true);
    }

    @Override
    protected void buildModels() {

        List<CampaignButtonModel_> buttonModels = new ArrayList<>();
        for (CampaignCategoryResponse item : categoryList)
            buttonModels.add(new CampaignButtonModel_()
                    .id(item.getSlug())
                    .clickListener((model, parentView, clickedView, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("model", model.model());
                        navController.navigate(R.id.campaignDetails, bundle);
                    })
                    .model(item));

        buttonCarousel
                .models(buttonModels)
                .onBind((model, view, position) -> {
                    StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setFullSpan(true);
                    view.setLayoutParams(params);
                })
                .addTo(this);

        for (CampaignProductResponse item : productList) {
            new CampaignProductModel_()
                    .id(item.getSlug())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> {
                        CampaignProductResponse item1 = model.model();
                        Intent intent = new Intent(activity, ViewProductActivity.class);
                        intent.putExtra("product_slug", item1.getSlug());
                        intent.putExtra("product_name", item1.getName());
                        intent.putExtra("product_price", item1.getPrice());
                        if (item.getShopSlug() != null)
                            intent.putExtra("shop_slug", item.getShopSlug());
                        intent.putExtra("product_image", item1.getImage());
                        intent.putExtra("cashback_text", item1.getCashbackText());
                        activity.startActivity(intent);
                    })
                    .addTo(this);
        }

        new NoItemModel_()
                .id("no_product_model")
                .text("No product found")
                .image(R.drawable.ic_empty_product)
                .width(100)
                .addIf(productList.size() == 0 && !isLoading, this);

        new LoadingModel_()
                .id("bottom_loading_model")
                .addIf(isLoading, this);

    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public void setProductList(List<CampaignProductResponse> productList) {
        this.productList = productList;
    }

    public void setCategoryList(List<CampaignCategoryResponse> categoryList) {
        this.categoryList = categoryList;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
