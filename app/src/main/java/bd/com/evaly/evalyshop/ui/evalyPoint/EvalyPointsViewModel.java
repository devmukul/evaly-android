package bd.com.evaly.evalyshop.ui.evalyPoint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListEntity;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.base.BaseViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EvalyPointsViewModel extends BaseViewModel {

    @Inject
    public EvalyPointsViewModel(SavedStateHandle savedStateHandle, ApiRepository apiRepository, PreferenceRepository preferenceRepository) {
        super(savedStateHandle, apiRepository, preferenceRepository);
    }
}
