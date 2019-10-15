package bd.com.evaly.evalyshop.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.models.db.AppDatabase;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import io.reactivex.Flowable;

public class RoomWIthRxViewModel extends AndroidViewModel {

    AppDatabase appDatabase;

    public RoomWIthRxViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppController.database;
    }


    public Flowable<List<RosterTable>> getList() {
        return appDatabase.taskDao().getAllRoster();
    }
}
