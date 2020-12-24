package bd.com.evaly.evalyshop.data.roomdb.cart;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;

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

    @Query("DELETE FROM cart_table WHERE product_id = :productID")
    void deleteBySlug(String productID);

    @Query("SELECT COUNT(id) FROM cart_table WHERE product_id = :productID")
    int checkExists(String productID);

    @Query("SELECT * FROM cart_table WHERE product_id = :productID LIMIT 1")
    List<CartEntity> checkExistsEntity(String productID);

    @Query("SELECT * FROM cart_table ORDER BY shop_slug, time desc")
    List<CartEntity> getAll();

    @Query("SELECT * FROM cart_table ORDER BY shop_slug, time desc")
    LiveData<List<CartEntity>> getAllLive();

    @Query("SELECT * FROM cart_table  WHERE is_selected = 1 ORDER BY shop_slug, time desc")
    LiveData<List<CartEntity>> getAllSelectedLive();

    @Query("SELECT COUNT(id) FROM cart_table")
    int getCount();

    @Query("SELECT COUNT(*) FROM cart_table")
    LiveData<Integer> getLiveCount();

    @Query("UPDATE cart_table SET quantity = :q WHERE product_id = :productID")
    void updateQuantity(String productID, int q);

    @Query("UPDATE cart_table SET is_selected = :s WHERE product_id = :productID")
    void markSelected(String productID, boolean s);

    @Query("UPDATE cart_table SET quantity = quantity+1 WHERE product_id = :productID")
    Completable rxIncreaseQuantity(String productID);

    @Query("UPDATE cart_table SET quantity = quantity-1 WHERE product_id = :productID AND quantity > 1")
    Completable rxDecreaseQuantity(String productID);

    @Query("UPDATE cart_table SET is_selected = :select")
    Completable rxSelectAll(boolean select);

    @Query("DELETE FROM cart_table WHERE product_id = :productID")
    Completable rxDeleteBySlug(String productID);

    @Query("UPDATE cart_table SET is_selected = :select WHERE product_id = :productID")
    Completable rxSelectById(String productID, boolean select);

    @Query("DELETE FROM cart_table")
    Completable rxDeleteAll();

    @Query("DELETE FROM cart_table WHERE is_selected = 1")
    Completable rxDeleteSelected();

}

