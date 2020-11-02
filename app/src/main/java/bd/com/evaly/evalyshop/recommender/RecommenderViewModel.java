package bd.com.evaly.evalyshop.recommender;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.recommender.database.table.RsDao;
import bd.com.evaly.evalyshop.recommender.database.table.RsEntity;
import bd.com.evaly.evalyshop.util.ToastUtils;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class RecommenderViewModel extends ViewModel {

    private RsDao rsDao;
    private CompositeDisposable compositeDisposable;
    private LiveData<List<RsEntity>> rsEntityLiveData;

    @Inject
    public RecommenderViewModel(RsDao rsDao) {
        this.rsDao = rsDao;
        this.rsEntityLiveData = rsDao.getAllLiveData();
        this.compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<RsEntity>> getRsEntityLiveData() {
        return rsEntityLiveData;
    }

    public void insert(String type, String slug, String name, String image) {
        compositeDisposable.add(
                rsDao.insertRx(new RsEntity(type, slug, name, image, Calendar.getInstance().getTimeInMillis(), 1, 1))
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Logger.e(e.toString());
                                updateOpenCount(type, slug);
                            }
                        }));
    }


    public void updateOpenCount(String type, String slug) {
        compositeDisposable.add(
                rsDao.updateOpenedCount(type, slug, Calendar.getInstance().getTimeInMillis())
                        .observeOn(Schedulers.io())
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
                        .observeOn(Schedulers.io())
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
                        .observeOn(Schedulers.io())
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
