package bd.com.evaly.evalyshop.recommender;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.recommender.database.table.RsDao;
import bd.com.evaly.evalyshop.recommender.database.table.RsEntity;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class RecommenderViewModel extends ViewModel {

    private RsDao rsDao;
    private CompositeDisposable compositeDisposable;
    private LiveData<List<RsEntity>> rsBrandLiveData;
    private LiveData<List<RsEntity>> rsCategoryLiveData;
    private LiveData<List<RsEntity>> rsShopLiveData;

    @Inject
    public RecommenderViewModel(RsDao rsDao) {
        this.rsDao = rsDao;
        this.rsBrandLiveData = rsDao.getLiveDataByType("brand");
        this.rsCategoryLiveData = rsDao.getLiveDataByType("category");
        this.rsShopLiveData = rsDao.getLiveDataByType("shop");
        this.compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<RsEntity>> getRsBrandLiveData() {
        return rsBrandLiveData;
    }

    public LiveData<List<RsEntity>> getRsCategoryLiveData() {
        return rsCategoryLiveData;
    }

    public LiveData<List<RsEntity>> getRsShopLiveData() {
        return rsShopLiveData;
    }

    public void insert(String type, String slug, String name, String image) {
        if (type.equals("shop") && name.contains("cyclone") && name.contains("flash"))
            return;
        compositeDisposable.add(
                rsDao.insertRx(new RsEntity(type, slug, name, image, Calendar.getInstance().getTimeInMillis(), 1, 1))
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                updateOpenCount(type, slug);
                            }
                        }));
    }


    public void updateOpenCount(String type, String slug) {
        compositeDisposable.add(
                rsDao.updateOpenedCount(type, slug, Calendar.getInstance().getTimeInMillis())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }));
    }


    public void updateSpentTime(String type, String slug, long duration) {
        compositeDisposable.add(
                rsDao.updateTimeSpent(type, slug, duration, Calendar.getInstance().getTimeInMillis())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }));
    }

    public void updateSpentTimeAndCount(String type, String slug, long duration) {
        compositeDisposable.add(
                rsDao.updateTimeSpentOpenCount(type, slug, duration, Calendar.getInstance().getTimeInMillis())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }));
    }

}
