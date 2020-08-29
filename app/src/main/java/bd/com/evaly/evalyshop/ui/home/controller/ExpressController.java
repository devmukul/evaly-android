package bd.com.evaly.evalyshop.ui.home.controller;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressItemModel_;
import bd.com.evaly.evalyshop.ui.home.model.HomeExpressSkeletonModel_;

public class ExpressController extends EpoxyController {

    private AppCompatActivity activity;
    private List<ExpressServiceModel> itemsExpress = new ArrayList<>();
    private Fragment fragment;

    @Override
    protected void buildModels() {

        for (int i = 0; i < 6; i++) {
            new HomeExpressSkeletonModel_()
                    .id("express_dummy" + i)
                    .addIf(itemsExpress.size() < 1, this);
        }

        for (ExpressServiceModel model : itemsExpress) {
            new HomeExpressItemModel_()
                    .clickListener((model1, parentView, clickedView, position) -> {
                        if (model1.getModel().getName().toLowerCase().contains("food")) {
                            try {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=bd.com.evaly.efood")));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=d.com.evaly.efood")));
                            }
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("model", model1.getModel());
                            NavHostFragment.findNavController(fragment).navigate(R.id.evalyExpressFragment, bundle);
                        }
                    })
                    .id(model.getSlug())
                    .model(model)
                    .addTo(this);
        }
    }

    public void addData(List<ExpressServiceModel> items) {
        this.itemsExpress.addAll(items);
        requestModelBuild();
    }

    public void reAddData(List<ExpressServiceModel> items) {
        this.itemsExpress = items;
        requestModelBuild();
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public List<ExpressServiceModel> getList() {
        return itemsExpress;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}