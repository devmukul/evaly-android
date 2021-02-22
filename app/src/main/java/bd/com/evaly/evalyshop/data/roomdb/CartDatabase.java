package bd.com.evaly.evalyshop.data.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import bd.com.evaly.evalyshop.data.roomdb.cart.CartDao;
import bd.com.evaly.evalyshop.data.roomdb.cart.CartEntity;

@Database(entities = {CartEntity.class}, version = 5, exportSchema = false)
public abstract class CartDatabase extends RoomDatabase {
    public abstract CartDao cartDao();
}
