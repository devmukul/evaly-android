package bd.com.evaly.evalyshop.recommender.module;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import bd.com.evaly.evalyshop.recommender.database.RecommenderDatabase;
import bd.com.evaly.evalyshop.recommender.database.table.RsDao;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import io.reactivex.disposables.CompositeDisposable;

@Module
@InstallIn(ApplicationComponent.class)
public class RecommenderModule {

    @Provides
    @Singleton
    public CompositeDisposable compositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    @Singleton
    public RecommenderDatabase appDatabase(Context context) {
        return Room.databaseBuilder(context, RecommenderDatabase.class, "recommender_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    public RsDao rsDao(RecommenderDatabase recommenderDatabase) {
        return recommenderDatabase.rsDao();
    }

}
