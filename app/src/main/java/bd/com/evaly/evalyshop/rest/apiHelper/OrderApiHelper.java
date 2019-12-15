package bd.com.evaly.evalyshop.rest.apiHelper;

import org.json.JSONObject;
import bd.com.evaly.evalyshop.listener.ResponseListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderApiHelper extends ApiHelper {


    public static void getOrderList(String token, int page, String orderStatus, ResponseListener<JSONObject, String> listener) {

        getiApiClient().getOrderList(token, page, orderStatus).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                listener.onDataFetched(response.body(), response.code());
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                listener.onFailed("error", 0);
            }
        });


    }



}
