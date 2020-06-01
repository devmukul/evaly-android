package bd.com.evaly.evalyshop.ui.shop;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory;

public class ShopViewModelFactory extends NewInstanceFactory{

    private String categorySlug;
    private String campaignSlug;
    private String shopSlug;

    public ShopViewModelFactory(String categorySlug, String campaignSlug, String shopSlug) {
        super();
        this.categorySlug = categorySlug;
        this.campaignSlug = campaignSlug;
        this.shopSlug = shopSlug;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShopViewModel.class)) {
            return (T) new ShopViewModel(categorySlug, campaignSlug, shopSlug);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
