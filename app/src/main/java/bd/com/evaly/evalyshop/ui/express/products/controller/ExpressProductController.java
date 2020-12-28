package bd.com.evaly.evalyshop.ui.express.products.controller;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.roomdb.express.ExpressServiceDao;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.ui.epoxyModels.LoadingModel_;
import bd.com.evaly.evalyshop.ui.epoxyModels.NoProductModel_;
import bd.com.evaly.evalyshop.ui.express.products.model.ExpressTitleModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeCarouselModelModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressHeaderModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressServiceModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressServiceSkeletonModel_;
import bd.com.evaly.evalyshop.util.ToastUtils;

public class ExpressProductController extends EpoxyController {

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
    private AppCompatActivity activity;
    private Fragment fragment;
    private List<ProductItem> items = new ArrayList<>();
    private List<ExpressServiceModel> itemsExpress = new ArrayList<>();
    private boolean loadingMore = false;
    private boolean emptyPage = false;
    private String title;
    private boolean isExpressLoading = true;
    private ExpressServiceDao expressServiceDao;

    public void setExpressServiceDao(ExpressServiceDao expressServiceDao) {
        this.expressServiceDao = expressServiceDao;
    }

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

        for (int i = 0; i < 12; i++) {
            new HomeExpressServiceSkeletonModel_()
                    .id("express_dummy" + i)
                    .addIf(isExpressLoading && itemsExpress.size() == 0, this);
        }

        for (ExpressServiceModel model : itemsExpress) {
            new HomeExpressServiceModel_()
                    .clickListener((model1, parentView, clickedView, position) -> {
                        if (model1.getModel().getSlug().equals("express-bullet-food")) {
                            Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage("bd.com.evaly.efood");
                            try {
                                if (launchIntent != null)
                                    activity.startActivity(launchIntent);
                                else
                                    openEfoodPlaystore();
                            } catch (ActivityNotFoundException e) {
                                openEfoodPlaystore();
                            }
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("model", model1.getModel());
                            NavHostFragment.findNavController(fragment).navigate(R.id.evalyExpressFragment, bundle);
                        }
                    })
                    .id(model.getSlug())
                    .model(model)
                    .fontSize(13)
                    .addTo(this);
        }
    }

    private void openEfoodPlaystore() {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "bd.com.evaly.efood")));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "bd.com.evaly.efood")));
        } catch (Exception e4) {
            ToastUtils.show("Please install eFood app from Playstore");
        }
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
