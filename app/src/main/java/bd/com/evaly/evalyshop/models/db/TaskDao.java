package bd.com.evaly.evalyshop.models.db;


import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;


public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAllRoster(List<RosterTable> list);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addRoster(RosterTable rosterTable);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAllAttribute(List<RosterTable> list);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAllAttributeValues(List<AttributeValuesTableModel> list);

    @Query("SELECT * FROM roster_table ORDER BY time DESC")
    Flowable<List<RosterTable>> getAllRoster();

    @Query("SELECT * FROM roster_table ORDER BY time DESC")
    List<RosterTable> getAllRosterWithoutObserve();

    @Query("DELETE from roster_table")
    void deleteAllRoster();

    @Query("UPDATE roster_table SET unread_count = :value WHERE id = :id")
    void updateUnreadCount(int value, String id);

    @Query("UPDATE roster_table SET last_message = :last_message, time = :time, unread_count = :count WHERE id = :id")
    void updateLastMessage(String last_message, long time, String id, int count);

    @Query("UPDATE roster_table SET status = :status WHERE id = :id")
    void updateStatus(int status, String id);
}
