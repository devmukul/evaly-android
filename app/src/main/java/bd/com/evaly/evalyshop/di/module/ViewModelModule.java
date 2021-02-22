package bd.com.evaly.evalyshop.di.module;

import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.CartDatabase;
import bd.com.evaly.evalyshop.data.roomdb.address.AddressListDao;
import bd.com.evaly.evalyshop.data.roomdb.banner.BannerDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.express.ExpressServiceDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@Module
@InstallIn(ViewModelComponent.class)
public final class ViewModelModule {
    @Provides
    AddressListDao addressListDao(AppDatabase appDatabase) {
        return appDatabase.addressListDao();
    }

    @Provides
    BannerDao bannerDao(AppDatabase appDatabase) {
        return appDatabase.bannerDao();
    }

    @Provides
    CartDao cartDao(CartDatabase cartDatabase) {
        return cartDatabase.cartDao();
    }

    @Provides
    ExpressServiceDao expressServiceDao(AppDatabase appDatabase) {
        return appDatabase.expressServiceDao();
    }

    @Provides
    WishListDao wishListDao(AppDatabase appDatabase) {
        return appDatabase.wishListDao();
    }
}
