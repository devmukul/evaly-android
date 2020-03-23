package bd.com.evaly.evalyshop.data.roomdb.categories;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insert(CategoryEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryEntity> list);

    @Delete
    void delete(CategoryEntity entity);

    @Query("DELETE FROM categories_table")
    void deleteAll();

    @Query("SELECT * FROM categories_table")
    List<CategoryEntity> getAll();

    @Query("SELECT COUNT(id) FROM categories_table")
    int getCount();

    @Query("SELECT * FROM categories_table")
    LiveData<List<CategoryEntity>> getAllLiveData();


}
