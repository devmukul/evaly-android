package bd.com.evaly.evalyshop.rest.apiHelper;

import java.util.List;

import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.order.OrderListItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderApiHelper extends ApiHelper {


    public static void getOrderList(String token, int page, String orderStatus, ResponseListenerAuth<CommonResultResponse<List<OrderListItem>>, String> listener) {


        if (orderStatus.equals("all"))
            orderStatus = null;

        getiApiClient().getOrderList(token, page, orderStatus).enqueue(new Callback<CommonResultResponse<List<OrderListItem>>>() {
            @Override
            public void onResponse(Call<CommonResultResponse<List<OrderListItem>>> call, Response<CommonResultResponse<List<OrderListItem>>> response) {
                listener.onDataFetched(response.body(), response.code());
            }

            @Override
            public void onFailure(Call<CommonResultResponse<List<OrderListItem>>> call, Throwable t) {
                listener.onFailed("error", 0);
            }
        });


    }



}
