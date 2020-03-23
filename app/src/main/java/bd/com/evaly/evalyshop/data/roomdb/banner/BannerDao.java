package bd.com.evaly.evalyshop.data.roomdb.banner;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import bd.com.evaly.evalyshop.models.banner.BannerItem;

@Dao
public interface BannerDao {

    @Insert
    void insert(BannerItem entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BannerItem> list);

    @Delete
    void delete(BannerItem entity);

    @Query("DELETE FROM banner_table")
    void deleteAll();

    @Query("SELECT * FROM banner_table ORDER BY  desc")
    List<BannerItem> getAll();

    @Query("SELECT * FROM banner_table ORDER BY id desc")
    LiveData<List<BannerItem>> getAllLive();

    @Query("SELECT COUNT(id) FROM banner_table")
    int getCount();

}
