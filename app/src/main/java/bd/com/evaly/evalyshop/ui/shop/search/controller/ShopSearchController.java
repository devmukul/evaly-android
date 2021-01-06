package bd.com.evaly.evalyshop.ui.shop.search.controller;

import android.content.Intent;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemShopProductSearchTitleBinding;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoProductModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeProductGridModel_;
import bd.com.evaly.evalyshop.ui.product.productDetails.ViewProductActivity;
import bd.com.evaly.evalyshop.ui.shop.search.ShopSearchViewModel;
import bd.com.evaly.evalyshop.ui.shop.search.model.ShopSearchProductTitleModel_;

public class ShopSearchController extends EpoxyController {

    @AutoModel
    LoadingModel_ loader;
    @AutoModel
    NoProductModel_ noProductModel;
    private List<ProductItem> list = new ArrayList<>();
    private ShopSearchViewModel viewModel;
    private int cashBack = 0;
    private AppCompatActivity activity;
    private boolean loadingMore = false;
    private String search = null;
    private boolean showSearchText = false;

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
    }

    public void setViewModel(ShopSearchViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setCashBack(int cashBack) {
        this.cashBack = cashBack;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setList(List<ProductItem> list) {
        this.list = list;
    }

    public void clearList() {
        list.clear();
    }

    public void setShowSearchText(boolean showSearchText) {
        this.showSearchText = showSearchText;
    }

    @Override
    protected void buildModels() {

        new ShopSearchProductTitleModel_()
                .id("search title")
                .search(search)
                .onBind((model, view, position) -> {
                    ItemShopProductSearchTitleBinding binding = (ItemShopProductSearchTitleBinding) view.getDataBinding();
                    if (model.search() != null)
                        binding.searchTitle.setText(model.search());
                })
                .addIf(search != null && !search.equals(""), this);

        for (ProductItem productItem : list) {
            new HomeProductGridModel_()
                    .id(productItem.getUniqueId())
                    .model(productItem)
                    .cashbackRate(cashBack)
                    .isShop(true)
                    .buyNowClickListener((model, parentView, clickedView, position) -> viewModel.setBuyNowLiveData(model.getModel().getSlug()))
                    .clickListener((model, parentView, clickedView, position) -> {
                        ProductItem item = model.getModel();
                        Intent intent = new Intent(activity, ViewProductActivity.class);
                        intent.putExtra("product_slug", item.getSlug());
                        intent.putExtra("product_name", item.getName());
                        intent.putExtra("product_price", (int) Double.parseDouble(item.getMaxPrice()));
                        if (model.cashbackRate() > 0)
                            intent.putExtra("cashback_text", model.cashbackRate() + "% Cashback");
                        if (model.isShop())
                            intent.putExtra("shop_slug", item.getShopSlug());
                        if (item.getImageUrls().size() > 0)
                            intent.putExtra("product_image", item.getImageUrls().get(0));
                        activity.startActivity(intent);
                    })
                    .addTo(this);
        }

        noProductModel
                .text(showSearchText ? "Search products here" : "No Products Available")
                .image(showSearchText ? R.drawable.ic_category_search : R.drawable.ic_empty_product)
                .onBind((model, view, position) -> {
                    TextView textView = view.findViewById(R.id.text);
                    ImageView imageView = view.findViewById(R.id.image);
                    Glide.with(view)
                            .asDrawable()
                            .load(model.image())
                            .into(imageView);
                    textView.setText(Html.fromHtml(model.text()));
                })
                .addIf(list.size() == 0 && !loadingMore, this);

        loader
                .onBind((model, view, position) -> {
                    StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.itemView.getLayoutParams();
                    params.setFullSpan(true);
                })
                .addIf(loadingMore, this);
    }
}
