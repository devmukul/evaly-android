package bd.com.evaly.evalyshop.ui.express.products;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.data.roomdb.express.ExpressServiceDao;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ExpressServiceViewModel extends ViewModel {

    private ApiRepository apiRepository;
    protected ExpressServiceDao expressServiceDao;

    @Inject
    public ExpressServiceViewModel(ApiRepository apiRepository, ExpressServiceDao expressServiceDao) {
        this.expressServiceDao = expressServiceDao;
        this.apiRepository = apiRepository;
        getExpressServices();
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


    public void getExpressServices() {
        apiRepository.getExpressServicesList(new ResponseListener<CommonDataResponse<List<ExpressServiceModel>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<ExpressServiceModel>> response, int statusCode) {
                syncList(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

        });
    }

}
