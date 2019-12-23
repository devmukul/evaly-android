package bd.com.evaly.evalyshop.data.roomdb.categories;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = CategoryEntity.class, version = 1, exportSchema = false)
public abstract class CategoryDatabase extends RoomDatabase {

    private static CategoryDatabase instance;

    public abstract CategoryDao categoryDao();


    public static synchronized CategoryDatabase getInstance(Context context){
        if (instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(), CategoryDatabase.class, "category_database")
                    .fallbackToDestructiveMigration()
                    .build();

        return instance;
    }

}
