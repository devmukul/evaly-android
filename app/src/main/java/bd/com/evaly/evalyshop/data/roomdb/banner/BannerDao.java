package bd.com.evaly.evalyshop.data.roomdb.banner;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import bd.com.evaly.evalyshop.models.banner.BannerItem;
import io.reactivex.Completable;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface BannerDao {

    @Insert(onConflict = REPLACE)
    void insert(BannerItem banner);

    @Insert(onConflict = REPLACE)
    void insertList(List<BannerItem> list);

    @Insert(onConflict = REPLACE)
    Completable insertListRx(List<BannerItem> list);

    @Query("DELETE FROM banner_table")
    void deleteAll();

    @Query("DELETE FROM banner_table WHERE banner_table.slug NOT IN(:list)")
    void deleteOld(List<String> list);

    @Query("DELETE FROM banner_table WHERE banner_table.slug NOT IN(:list)")
    Completable deleteOldRx(List<String> list);

    @Query("DELETE FROM banner_table WHERE banner_table.slug != :slug")
    Completable deleteOldBySlugRx(String slug);

    @Query("SELECT * FROM banner_table")
    LiveData<List<BannerItem>> getAll();

}
