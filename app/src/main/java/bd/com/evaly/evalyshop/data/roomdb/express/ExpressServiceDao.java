package bd.com.evaly.evalyshop.data.roomdb.express;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ExpressServiceDao {

    @Insert(onConflict = REPLACE)
    void insert(ExpressServiceModel expressServiceModel);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertList(List<ExpressServiceModel> list);

    @Query("DELETE FROM express_service_list")
    void deleteAll();

    @Query("DELETE FROM express_service_list WHERE express_service_list.slug NOT IN(:list)")
    void deleteOld(List<String> list);

    @Query("SELECT * FROM express_service_list")
    LiveData<List<ExpressServiceModel>> getAll();

}
