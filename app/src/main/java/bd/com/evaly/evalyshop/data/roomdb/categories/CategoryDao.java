package bd.com.evaly.evalyshop.data.roomdb.categories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CategoryDao {

    @Insert(onConflict = REPLACE)
    void insert(CategoryEntity entity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<CategoryEntity> list);

    @Delete
    void delete(CategoryEntity entity);

    @Query("DELETE FROM categories_table")
    void deleteAll();

    @Query("SELECT * FROM categories_table")
    List<CategoryEntity> getAll();

    @Query("SELECT COUNT(slug) FROM categories_table")
    int getCount();

    @Query("SELECT COUNT(slug) FROM categories_table")
    LiveData<Integer> getCountLive();

    @Query("SELECT * FROM categories_table")
    LiveData<List<CategoryEntity>> getAllLiveData();

    @Query("DELETE FROM categories_table WHERE categories_table.slug NOT IN(:list)")
    void deleteOld(List<String> list);

}
