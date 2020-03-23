package bd.com.evaly.evalyshop.ui.home.controller;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.airbnb.epoxy.EpoxyController;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.ui.home.model.HomeSliderItemModel_;
import bd.com.evaly.evalyshop.util.Utils;

public class SliderController extends EpoxyController {

    private AppCompatActivity activity;
    private List<BannerItem> items = new ArrayList<>();

    @Override
    protected void buildModels() {

        for (BannerItem item : items) {
            new HomeSliderItemModel_()
                    .id(item.getSlug())
                    .model(item)
                    .clickListener((model, parentView, clickedView, position) -> {

                        BannerItem item1 = model.getModel();

                        String url = item1.getUrl();

                        if (url.equals("") || url.equals("https://evaly.com.bd") || url.equals("https://evaly.com.bd/")) {
                            Toast.makeText(activity, "It's just a banner. No page to open.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (item1.getUrl().contains("evaly.com.bd/shops")) {
                            String[] ar = item1.getUrl().split("/shops/");
                            if (ar.length > 1) {
                                String slug = ar[1];
                                Bundle bundle = new Bundle();
                                bundle.putInt("type", 1);
                                bundle.putString("shop_name", item1.getName());
                                bundle.putString("shop_slug", slug);
                                bundle.putString("category", "root");
                                Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.shopFragment, bundle);
                            } else
                                Toast.makeText(activity, "It's just a banner. No page to open.", Toast.LENGTH_SHORT).show();
                        } else {

                            Utils.CustomTab(item1.getUrl(), activity);
                        }

                    })
                    .addTo(this);
        }

    }

    public void addData(List<BannerItem> items) {
        this.items.addAll(items);
        requestModelBuild();
    }

    public void reAddData(List<BannerItem> items){
        this.items.clear();
        this.items.addAll(items);
        requestModelBuild();

    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }


}