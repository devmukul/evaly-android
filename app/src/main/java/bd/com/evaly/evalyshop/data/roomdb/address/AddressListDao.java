package bd.com.evaly.evalyshop.data.roomdb.address;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import bd.com.evaly.evalyshop.models.profile.AddressResponse;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AddressListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(AddressResponse entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<AddressResponse> list);

    @Delete
    void delete(AddressResponse entity);

    @Query("DELETE FROM address_list_table")
    Completable deleteAll();

    @Query("DELETE FROM address_list_table WHERE id = :id")
    Completable deleteById(int id);

    @Query("DELETE FROM address_list_table WHERE id NOT IN (:id)")
    Completable deleteByIds(List<String> id);

    @Query("SELECT * FROM address_list_table")
    LiveData<List<AddressResponse>> getAllLive();

    @Query("SELECT * FROM address_list_table ORDER BY `primary`")
    LiveData<List<AddressResponse>> getAllPrimaryLive();

    @Query("SELECT * FROM address_list_table ORDER BY `primary`")
    Single<List<AddressResponse>> getAllPrimary();

    @Query("SELECT COUNT(id) FROM address_list_table")
    int getCount();

    @Query("SELECT COUNT(*) FROM address_list_table")
    LiveData<Integer> getLiveCount();

}
