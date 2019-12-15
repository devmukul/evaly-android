package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GeneralApiHelper extends ApiHelper{


    public static void getRootCategories(ResponseListenerAuth<List<CategoryEntity>, String> listener) {


        getiApiClient().getRootCategories().enqueue(new Callback<List<CategoryEntity>>() {
            @Override
            public void onResponse(Call<List<CategoryEntity>> call, Response<List<CategoryEntity>> response) {
                listener.onDataFetched(response.body(), response.code());
            }

            @Override
            public void onFailure(Call<List<CategoryEntity>> call, Throwable t) {
                listener.onFailed("error", 0);
            }
        });


    }

}
