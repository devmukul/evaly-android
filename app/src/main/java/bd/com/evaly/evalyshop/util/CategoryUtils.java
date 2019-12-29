package bd.com.evaly.evalyshop.util;


import android.content.Context;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import bd.com.evaly.evalyshop.data.roomdb.AppDatabase;
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.util.preference.MyPreference;
import bd.com.evaly.evalyshop.rest.apiHelper.GeneralApiHelper;

public class CategoryUtils {

    private Context context;
    private AppDatabase appDatabase;

    public CategoryUtils(Context context) {
        this.context = context;
        appDatabase = AppDatabase.getInstance(context);

    }

    public long getLastUpdated(){
        return MyPreference.with(context, "category_db_new11").getLong("last_updated", 0);
    }

    public void setLastUpdated(){
        Calendar calendar = Calendar.getInstance();
        MyPreference.with(context, "category_db_new11").addLong("last_updated", calendar.getTimeInMillis()).save();
    }

    public void getLocalCategoryList(DataFetchingListener<List<CategoryEntity>> listener) {
        Executors.newSingleThreadExecutor().execute(() -> listener.onDataFetched(appDatabase.categoryDao().getAll()));
    }

    public void updateFromApi(DataFetchingListener<List<CategoryEntity>> listener){

        GeneralApiHelper.getRootCategories(new ResponseListenerAuth<List<CategoryEntity>, String>() {
            @Override
            public void onDataFetched(List<CategoryEntity> response, int statusCode) {

                if (response != null) {
//                    for (int i = 0; i < response.size(); i++)
//                        response.get(i).setDrawable(getDrawableFromName(response.get(i).getName()));

                    setLastUpdated();

                    Executors.newSingleThreadExecutor().execute(() -> {
                        appDatabase.categoryDao().deleteAll();
                        appDatabase.categoryDao().insertAll(response);
                    });

                    listener.onDataFetched(response);
                } else {
                    listener.onFailed(0);
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }




}
