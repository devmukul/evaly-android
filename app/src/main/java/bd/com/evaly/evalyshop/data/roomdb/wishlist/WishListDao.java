package bd.com.evaly.evalyshop.data.roomdb.wishlist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WishListDao {

    @Insert
    void insert(WishListEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<WishListEntity> list);

    @Delete
    void delete(WishListEntity entity);

    @Query("DELETE FROM wishlist_table")
    void deleteAll();

    @Query("DELETE FROM wishlist_table WHERE slug = :productSlug")
    void deleteBySlug(String productSlug);

    @Query("SELECT COUNT(id) FROM wishlist_table WHERE slug = :productSlug")
    int checkExists(String productSlug);

    @Query("SELECT * FROM wishlist_table")
    List<WishListEntity> getAll();

    @Query("SELECT COUNT(id) FROM wishlist_table")
    int getCount();

    @Query("SELECT COUNT(*) FROM wishlist_table")
    LiveData<Integer> getLiveCount();


}
