package bd.com.evaly.evalyshop.recommender.database;



import androidx.room.Database;
import androidx.room.RoomDatabase;

import bd.com.evaly.evalyshop.recommender.database.table.RsDao;
import bd.com.evaly.evalyshop.recommender.database.table.RsEntity;

@Database(entities = RsEntity.class, version = 1, exportSchema = false)
public abstract class RecommenderDatabase extends RoomDatabase {

    public abstract RsDao rsDao();

}
