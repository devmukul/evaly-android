package bd.com.evaly.evalyshop.ui.express.products.controller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoProductModel_;
import bd.com.evaly.evalyshop.ui.express.products.model.ExpressTitleModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeCarouselModelModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressHeaderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressItemModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressSkeletonModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeTabsModel_;

public class ExpressProductController extends EpoxyController {

    private AppCompatActivity activity;
    private Fragment fragment;
    private List<ProductItem> items = new ArrayList<>();
    private List<ExpressServiceModel> itemsExpress = new ArrayList<>();
    private AppDatabase appDatabase;
    private boolean loadingMore = false;
    private boolean emptyPage = false;
    private String title;
    private boolean isExpressLoading = true;

    @AutoModel
    HomeExpressModel_ expressModel;

    @AutoModel
    HomeTabsModel_ tabsModel;

    @AutoModel
    LoadingModel_ loader;

    @AutoModel
    ExpressTitleModel_ titleModel;

    @AutoModel
    NoProductModel_ noProductModel;

    @AutoModel
    HomeCarouselModelModel_ expressCarousel;

    @AutoModel
    HomeExpressHeaderModel_ expressHeaderModel;

    public void showEmptyPage(boolean emptyPage, boolean build) {
        this.emptyPage = emptyPage;
        if (build)
            requestModelBuild();
    }


    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
//
//        expressHeaderModel
//                .addTo(this);

        List<HomeExpressSkeletonModel_> skeletons = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            new HomeExpressSkeletonModel_()
                    .id("express_dummy" + i)
                    .addIf(isExpressLoading && itemsExpress.size() == 0, this);
        }

        List<HomeExpressItemModel_> expressServiceModels = new ArrayList<>();
        for (ExpressServiceModel model : itemsExpress) {
            new HomeExpressItemModel_()
                    .clickListener((model1, parentView, clickedView, position) -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("model", model1.getModel());
                        NavHostFragment.findNavController(fragment).navigate(R.id.evalyExpressFragment, bundle);
                    })
                    .id(model.getSlug())
                    .model(model)
                    .addTo(this);
        }

//        expressCarousel
//                .onBind((model, view, position) -> {
//                    if (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
//                        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
//                        params.setFullSpan(true);
//                    } else {
//                        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
//                                ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT
//                        );
//                        params.setFullSpan(true);
//                        view.setLayoutParams(params);
//                    }
//                    view.setBackgroundColor(Color.WHITE);
//                })
//                .padding(new Carousel.Padding(
//                        (int) Utils.convertDpToPixel(5, activity),
//                        (int) Utils.convertDpToPixel(5, activity), 20,
//                        (int) Utils.convertDpToPixel(20, activity),
//                        0))
//                .models(isExpressLoading && expressServiceModels.size() == 0 ? skeletons : expressServiceModels)
//                .addTo(this);

//        expressModel
//                .fragment(fragment)
//                .activity(activity)
//                .appDatabase(appDatabase)
//                .addTo(this);

//        titleModel
//                .title(title)
//                .addTo(this);
//
//        for (ProductItem productItem : items) {
//            new HomeProductGridModel_()
//                    .id(productItem.getUniqueId())
//                    .model(productItem)
//                    .clickListener((model, parentView, clickedView, position) -> {
//                        ProductItem item = model.getModel();
//                        Intent intent = new Intent(activity, ViewProductActivity.class);
//                        intent.putExtra("product_slug", item.getSlug());
//                        intent.putExtra("product_name", item.getName());
//                        intent.putExtra("product_price", item.getMaxPrice());
//                        if (item.getImageUrls().size() > 0)
//                            intent.putExtra("product_image", item.getImageUrls().get(0));
//                        activity.startActivity(intent);
//                    })
//                    .addTo(this);
//        }
//
//
//        noProductModel
//                .text("No Products Found")
//                .image(R.drawable.ic_empty_product)
//                .addIf(emptyPage, this);
//
//        loader.addIf(loadingMore, this);
    }

    public void setItemsExpress(List<ExpressServiceModel> itemsExpress) {
        this.itemsExpress = itemsExpress;
        requestModelBuild();
    }

    public void addData(List<ProductItem> productItems) {
        this.items.addAll(productItems);
        requestModelBuild();
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
        appDatabase = AppDatabase.getInstance(activity);
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void clear() {
        items.clear();
        requestModelBuild();
    }

    public int listSize() {
        return items.size();
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
