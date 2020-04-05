package bd.com.evaly.evalyshop.data.roomdb;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import bd.com.evaly.evalyshop.data.roomdb.userInfo.UserInfoDao;
import bd.com.evaly.evalyshop.data.roomdb.userInfo.UserInfoEntity;

@Database(entities = {UserInfoEntity.class}, version = 3, exportSchema = false)
public abstract class ProviderDatabase extends RoomDatabase {

    private static ProviderDatabase instance;

    public static synchronized ProviderDatabase getInstance(Context context) {
        if (instance == null) {
            Log.d("hmt", "data new instance");
            instance = Room.databaseBuilder(context.getApplicationContext(), ProviderDatabase.class, "provider_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

    public abstract UserInfoDao userInfoDao();
}
