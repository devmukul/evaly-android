package bd.com.evaly.evalyshop.data.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryDao;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListDao;
import bd.com.evaly.evalyshop.data.roomdb.wishlist.WishListEntity;

@Database(entities = {CategoryEntity.class, WishListEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public abstract CategoryDao categoryDao();
    public abstract WishListDao wishListDao();

    public static synchronized AppDatabase getInstance(Context context){
        if (instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .build();

        return instance;
    }
}
