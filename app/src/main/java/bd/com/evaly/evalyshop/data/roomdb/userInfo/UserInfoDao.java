package bd.com.evaly.evalyshop.data.roomdb.userInfo;


import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserInfoEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserInfoEntity> list);

    @Delete
    void delete(UserInfoEntity entity);

    @Query("DELETE FROM user_info_table")
    void deleteAll();

    @Query("SELECT * FROM user_info_table")
    Cursor getCursor();

    @Query("SELECT * FROM user_info_table")
    List<UserInfoEntity> getAll();
    
    @Query("SELECT COUNT(token) FROM user_info_table")
    int getCount();




}

