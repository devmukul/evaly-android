package bd.com.evaly.evalyshop.models.campaign.topProducts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.models.campaign.category.CampaignCategoryResponse;
import bd.com.evaly.evalyshop.models.campaign.products.CampaignProductResponse;

public class CampaignTopProductResponse extends CampaignCategoryResponse implements Serializable {

    @SerializedName("items")
    private List<CampaignProductResponse> products;

    public List<CampaignProductResponse> getProducts() {
        if (products == null)
            return new ArrayList<>();
        return products;
    }

    public void setProducts(List<CampaignProductResponse> products) {
        this.products = products;
    }
}