package bd.com.evaly.evalyshop.data.roomdb;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import bd.com.evaly.evalyshop.data.roomdb.banner.BannerDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryDao;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.data.roomdb.express.ExpressServiceDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListEntity;
import bd.com.evaly.evalyshop.models.banner.BannerItem;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;

@Database(entities = {CategoryEntity.class, WishListEntity.class, CartEntity.class, BannerItem.class, ExpressServiceModel.class}, version = 16, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public abstract CategoryDao categoryDao();
    public abstract WishListDao wishListDao();
    public abstract CartDao cartDao();
    public abstract BannerDao bannerDao();
    public abstract ExpressServiceDao expressServiceDao();

    public static synchronized AppDatabase getInstance(Context context){
        if (instance == null) {
            Log.d("hmt", "data new instance");
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
