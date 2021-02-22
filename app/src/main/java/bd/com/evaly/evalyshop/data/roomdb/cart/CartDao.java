package bd.com.evaly.evalyshop.data.roomdb.cart;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface CartDao {

    @Insert
    void insert(CartEntity entity);

    @Insert
    Completable insertRx(CartEntity entity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertIgnore(CartEntity entity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertAllIgnore(List<CartEntity> list);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CartEntity> list);

    @Delete
    void delete(CartEntity entity);

    @Query("DELETE FROM cart_table")
    void deleteAll();

    @Query("DELETE FROM cart_table WHERE cart_table.shop_item_id NOT IN(:list)")
    Completable deleteOldRx(List<String> list);

    @Query("DELETE FROM cart_table WHERE shop_item_id = :productID")
    void deleteBySlug(String productID);

    @Query("SELECT COUNT(shop_item_id) FROM cart_table WHERE shop_item_id = :productID")
    int checkExists(String productID);

    @Query("SELECT * FROM cart_table WHERE shop_item_id = :productID LIMIT 1")
    List<CartEntity> checkExistsEntity(String productID);

    @Query("SELECT * FROM cart_table WHERE shop_item_id = :productID LIMIT 1")
    Single<List<CartEntity>> checkExistsRx(String productID);

    @Query("SELECT * FROM cart_table ORDER BY shop_slug, time desc")
    Single<List<CartEntity>> getListRx();

    @Query("SELECT * FROM cart_table ORDER BY shop_slug, time desc")
    List<CartEntity> getAll();

    @Query("SELECT * FROM cart_table ORDER BY shop_slug, time desc")
    LiveData<List<CartEntity>> getAllLive();

    @Query("SELECT * FROM cart_table  WHERE is_selected = 1 ORDER BY shop_slug, time desc")
    LiveData<List<CartEntity>> getAllSelectedLive();

    @Query("SELECT COUNT(shop_item_id) FROM cart_table")
    int getCount();

    @Query("SELECT COUNT(*) FROM cart_table")
    LiveData<Integer> getLiveCount();

    @Query("UPDATE cart_table SET quantity = :q WHERE shop_item_id = :productID")
    void updateQuantity(String productID, int q);

    @Query("UPDATE cart_table SET is_selected = :s WHERE shop_item_id = :productID")
    void markSelected(String productID, boolean s);

    @Query("UPDATE cart_table SET quantity = quantity+1 WHERE shop_item_id = :productID")
    Completable rxIncreaseQuantity(String productID);

    @Query("UPDATE cart_table SET quantity = quantity-1 WHERE shop_item_id = :productID AND quantity > 1")
    Completable rxDecreaseQuantity(String productID);

    @Query("UPDATE cart_table SET is_selected = :select")
    Completable rxSelectAll(boolean select);

    @Query("DELETE FROM cart_table WHERE shop_item_id = :productID")
    Completable rxDeleteBySlug(String productID);

    @Query("UPDATE cart_table SET is_selected = :select WHERE shop_item_id = :productID")
    Completable rxSelectById(String productID, boolean select);

    @Query("DELETE FROM cart_table")
    Completable rxDeleteAll();

    @Query("DELETE FROM cart_table WHERE is_selected = 1")
    Completable rxDeleteSelected();

}

