package bd.com.evaly.evalyshop.ui.campaign.bottomsheet;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.BaseModel;
import bd.com.evaly.evalyshop.models.campaign.campaign.SubCampaignResponse;
import bd.com.evaly.evalyshop.models.campaign.category.CampaignProductCategoryResponse;
import bd.com.evaly.evalyshop.ui.campaign.bottomsheet.model.ProductCategoryListModel_;
import bd.com.evaly.evalyshop.ui.campaign.model.CampaignSubModel_;
import bd.com.evaly.evalyshop.ui.epoxy.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxy.NoItemModel_;

public class CampaignFilterController extends EpoxyController {

    private List<BaseModel> list = new ArrayList<>();
    private boolean isLoading = true;
    private ClickListener clickListener;


    public interface ClickListener {
        void onClick(SubCampaignResponse model);
        void onProductCategoryClick(CampaignProductCategoryResponse model);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    protected void buildModels() {
        for (BaseModel item : list) {
            if (item instanceof SubCampaignResponse) {
                new CampaignSubModel_()
                        .id(((SubCampaignResponse) item).getSlug())
                        .model((SubCampaignResponse) item)
                        .clickListener((model, parentView, clickedView, position) -> {
                            clickListener.onClick(model.model());
                        })
                        .addTo(this);

            } else if (item instanceof CampaignProductCategoryResponse) {
                new ProductCategoryListModel_()
                        .id("category", ((CampaignProductCategoryResponse) item).getCategorySlug() + ((CampaignProductCategoryResponse) item).getShopSlug())
                        .clickListener((model, parentView, clickedView, position) -> {
                            clickListener.onProductCategoryClick(model.model());
                        })
                        .model((CampaignProductCategoryResponse) item)
                        .addTo(this);
            }
        }

        new NoItemModel_()
                .id("no_product_model")
                .text("Nothing found")
                .image(R.drawable.ic_discount_tag)
                .width(80)
                .addIf(list.size() == 0 && !isLoading, this);

        new LoadingModel_()
                .id("bottom_loading_model")
                .addIf(isLoading, this);
    }

    public void setList(List<BaseModel> list) {
        this.list = list;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
