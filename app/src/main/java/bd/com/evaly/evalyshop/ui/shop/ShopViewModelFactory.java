package bd.com.evaly.evalyshop.ui.shop;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory;

import bd.com.evaly.evalyshop.ui.shop.quickView.ShopQuickViewModel;

public class ShopViewModelFactory extends NewInstanceFactory{

    private String categorySlug;
    private String campaignSlug;
    private String shopSlug;
    private String brandSlug;

    public ShopViewModelFactory(String categorySlug, String campaignSlug, String shopSlug, String brandSlug) {
        super();
        this.categorySlug = categorySlug;
        this.campaignSlug = campaignSlug;
        this.shopSlug = shopSlug;
        this.brandSlug = brandSlug;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShopViewModel.class)) {
            return (T) new ShopViewModel(categorySlug, campaignSlug, shopSlug, brandSlug);
        }

        if (modelClass.isAssignableFrom(ShopQuickViewModel.class)) {
            return (T) new ShopQuickViewModel(categorySlug, campaignSlug, shopSlug, brandSlug);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
