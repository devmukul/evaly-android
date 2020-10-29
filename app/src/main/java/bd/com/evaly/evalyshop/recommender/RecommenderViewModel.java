package bd.com.evaly.evalyshop.recommender;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.recommender.database.table.RsDao;

public class RecommenderViewModel extends ViewModel {

    private RsDao rsDao;

    @Inject
    public RecommenderViewModel(RsDao rsDao) {
        this.rsDao = rsDao;
    }

}
