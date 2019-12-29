package bd.com.evaly.evalyshop.data.roomdb.wishlist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;

@Dao
public interface WishListDao {

    @Insert
    void insert(WishListEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<WishListEntity> list);

    @Delete
    void delete(CategoryEntity entity);

    @Query("DELETE FROM wishlist_table")
    void deleteAll();

    @Query("SELECT * FROM wishlist_table")
    List<WishListEntity> getAll();

}
