package bd.com.evaly.evalyshop.ui.campaign.controller;

import android.view.ViewGroup;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignButtonModel_;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignProductModel_;
import bd.com.evaly.evalyshop.ui.campaign.model.CategoryCarouselModel_;

public class CampaignController extends EpoxyController {

    @AutoModel
    CategoryCarouselModel_ buttonCarousel;
    private List<CampaignCategoryResponse> categoryList = new ArrayList<>();
    private List<CampaignProductResponse> productList = new ArrayList<>();

    @Override
    protected void buildModels() {

        List<CampaignButtonModel_> buttonModels = new ArrayList<>();
        for (CampaignCategoryResponse item : categoryList)
            buttonModels.add(new CampaignButtonModel_()
                    .id(item.getSlug())
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
                    .addTo(this);
        }

    }

    public void setProductList(List<CampaignProductResponse> productList) {
        this.productList = productList;
    }

    public void setCategoryList(List<CampaignCategoryResponse> categoryList) {
        this.categoryList = categoryList;
    }
}
