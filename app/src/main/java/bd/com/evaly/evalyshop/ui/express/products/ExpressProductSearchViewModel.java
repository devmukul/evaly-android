package bd.com.evaly.evalyshop.ui.express.products;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.express.ExpressServiceDao;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ExpressProductSearchViewModel extends ViewModel {

    ExpressServiceDao expressServiceDao;

    @Inject
    public ExpressProductSearchViewModel(ExpressServiceDao expressServiceDao) {
        this.expressServiceDao = expressServiceDao;
    }

    public void syncList(List<ExpressServiceModel> list) {
        Executors.newFixedThreadPool(4).execute(() -> {
            expressServiceDao.insertList(list);

            List<String> slugs = new ArrayList<>();
            for (ExpressServiceModel item : list)
                slugs.add(item.getSlug());

            if (slugs.size() > 0)
                expressServiceDao.deleteOld(slugs);
        });
    }

}
