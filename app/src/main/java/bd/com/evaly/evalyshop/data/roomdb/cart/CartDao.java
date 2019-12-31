package bd.com.evaly.evalyshop.data.roomdb.cart;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CartDao {

    @Insert
    void insert(CartEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CartEntity> list);

    @Delete
    void delete(CartEntity entity);

    @Query("DELETE FROM cart_table")
    void deleteAll();

    @Query("DELETE FROM cart_table WHERE slug = :productSlug")
    void deleteBySlug(String productSlug);

    @Query("SELECT COUNT(id) FROM cart_table WHERE slug = :productSlug")
    int checkExists(String productSlug);

    @Query("SELECT * FROM cart_table WHERE slug = :productSlug LIMIT 1")
    List<CartEntity> checkExistsEntity(String productSlug);

    @Query("SELECT * FROM cart_table")
    List<CartEntity> getAll();

    @Query("SELECT * FROM cart_table")
    LiveData<List<CartEntity>> getAllLive();

    @Query("SELECT COUNT(id) FROM cart_table")
    int getCount();

    @Query("SELECT COUNT(*) FROM cart_table")
    LiveData<Integer> getLiveCount();

    @Query("UPDATE cart_table SET quantity = :q WHERE slug = :slug")
    void updateQuantity(String slug, int q);

    @Query("UPDATE cart_table SET is_selected = :s WHERE slug = :slug")
    void markSelected(String slug, boolean s);


}

