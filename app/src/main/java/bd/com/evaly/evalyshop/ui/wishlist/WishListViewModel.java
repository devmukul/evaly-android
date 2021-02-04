package bd.com.evaly.evalyshop.ui.wishlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListEntity;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WishListViewModel extends ViewModel {

    private WishListDao wishListDao;
    public LiveData<List<WishListEntity>> liveData;

    @Inject
    public WishListViewModel(WishListDao wishListDao){
        this.wishListDao = wishListDao;
        liveData = wishListDao.getAllLive();
    }

    public void deleteBySlug(String slug){
        Executors.newSingleThreadExecutor().execute(() -> wishListDao.deleteBySlug(slug));
    }

}
