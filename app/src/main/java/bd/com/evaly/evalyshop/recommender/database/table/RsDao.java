package bd.com.evaly.evalyshop.recommender.database.table;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RsDao {

    @Insert(onConflict = REPLACE)
    void insert(RsEntity entity);

    @Insert
    Completable insertRx(RsEntity entity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<RsEntity> list);

    @Delete
    void delete(RsEntity entity);

    @Query("DELETE FROM recommender_table")
    void deleteAll();

    @Query("SELECT * FROM recommender_table")
    List<RsEntity> getAll();

    @Query("SELECT COUNT(slug) FROM recommender_table")
    int getCount();

    @Query("SELECT COUNT(slug) FROM recommender_table")
    LiveData<Integer> getCountLive();

    @Query("SELECT * FROM recommender_table")
    LiveData<List<RsEntity>> getAllLiveData();

    @Query("SELECT * FROM recommender_table WHERE type = :type ORDER BY open_count DESC")
    LiveData<List<RsEntity>> getLiveDataByType(String type);

    // new events

    @Query("SELECT * FROM recommender_table ORDER BY open_count, time_spent DESC")
    LiveData<List<RsEntity>> getLiveDataSorted();

    @Query("UPDATE recommender_table SET open_count = open_count+1, last_opened = :time WHERE type = :type AND slug = :slug")
    Completable updateOpenedCount(String type, String slug, long time);

    @Query("UPDATE recommender_table SET time_spent = time_spent+:duration, last_opened = :time WHERE type = :type AND slug = :slug")
    Completable updateTimeSpent(String type, String slug, long duration, long time);

    @Query("UPDATE recommender_table SET time_spent = time_spent+:duration, last_opened = :time, open_count = open_count+1 WHERE type = :type AND slug = :slug")
    Completable updateTimeSpentOpenCount(String type, String slug, long duration, long time);

}
