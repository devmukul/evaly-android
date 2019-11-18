package bd.com.evaly.evalyshop.util;


import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.models.category.CategoryItem;
import bd.com.evaly.evalyshop.preference.MyPreference;

public class CategoryUtils {

    private Context context;

    public CategoryUtils(Context context) {
        this.context = context;
    }

    public void saveCategoryJson(String json) {
        MyPreference.with(context, "category_db").addString("category_json", json).save();

    }

    public String getCategoryJson() {
        return MyPreference.with(context, "category_db").getString("category_json", "");
    }

    public long getLastUpdated(){
        return MyPreference.with(context, "category_db").getLong("last_updated", 0);
    }

    public void setLastUpdated(){

        Calendar calendar = Calendar.getInstance();

        Log.d("jsonz response time set", calendar.getTimeInMillis() + "");

        MyPreference.with(context, "category_db").addLong("last_updated", calendar.getTimeInMillis()).save();
    }

    public ArrayList<CategoryItem> getCategoryArrayList(String json) {

        ArrayList<CategoryItem> list = new ArrayList<>();

        JSONArray jsonArray;

        try {

            if (json.equals(""))
                jsonArray = new JSONArray(getCategoryJson());
            else
            jsonArray = new JSONArray(json);

        } catch (Exception e){
            jsonArray = new JSONArray();
        }

        for (int i=0; i < jsonArray.length(); i++){

            try {
                Gson gson = new Gson();
                CategoryItem item = gson.fromJson(jsonArray.getJSONObject(i).toString(), CategoryItem.class);
                item.setDrawable(getDrawableFromName(item.getName()));
                list.add(item);

            } catch (Exception e){
            }

        }

        return list;

    }



    public void updateFromApi(DataFetchingListener<ArrayList<CategoryItem>> listener) {

        String url = UrlUtils.DOMAIN + "core/public/categories/";


        Log.d("jsonz url", url);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("jsonz response", response);

                setLastUpdated();
                saveCategoryJson(response);
                listener.onDataFetched(getCategoryArrayList(response));
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
                try { listener.onFailed(error.networkResponse.statusCode); } catch (Exception e) { }
            }

        });

        RequestQueue queue = Volley.newRequestQueue(context);
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);


    }



    private int getDrawableFromName(String name){

        int drawable = 0;

        if(name.contains("Bags"))
            drawable = R.drawable.ic_bags_set;
        else if(name.contains("Beauty"))
            drawable = R.drawable.ic_color_lotion;
        else if(name.contains("Books"))
            drawable = R.drawable.ic_color_books;
        else if(name.contains("Construction"))
            drawable = R.drawable.ic_color_construction;
        else if(name.contains("Decoration"))
            drawable = R.drawable.ic_color_decoration;
        else if(name.contains("Electronics"))
            drawable = R.drawable.ic_color_multiple_devices;
        else if(name.contains("Electric"))
            drawable = R.drawable.ic_color_electric;
        else if(name.contains("Event"))
            drawable = R.drawable.ic_color_event;
        else if(name.contains("Food & Beverage"))
            drawable = R.drawable.ic_color_beverage;
        else if(name.contains("Burmese"))
            drawable = R.drawable.burmes_item;
        else if(name.contains("Dhaka Bank"))
            drawable = R.drawable.dhaka_bank_logo;
        else if(name.contains("Food & Restaurants"))
            drawable = R.drawable.ic_color_food_plate;
        else if(name.contains("Furniture"))
            drawable = R.drawable.ic_color_sliding_door_closet;
        else if(name.contains("Glasses"))
            drawable = R.drawable.ic_color_glasses_new;
        else if(name.contains("Grocery"))
            drawable = R.drawable.ic_color_ingredients;
        else if(name.contains("Handmade"))
            drawable = R.drawable.ic_color_potters_wheel;
        else if(name.contains("Harvesting & Agriculture"))
            drawable = R.drawable.ic_color_harvest;
        else if(name.contains("Health"))
            drawable = R.drawable.ic_color_health_checkup_1;
        else if(name.contains("Home & Living"))
            drawable = R.drawable.ic_color_open_curtains;
        else if(name.contains("Home Garden"))
            drawable = R.drawable.ic_color_orchid;
        else if(name.contains("Hotel"))
            drawable = R.drawable.ic_color_hotel_building;
        else if(name.contains("Jewellery"))
            drawable = R.drawable.ic_color_jewelry;
        else if(name.contains("Kids"))
            drawable = R.drawable.ic_color_kids;
        else if(name.contains("Kitchen & Dining"))
            drawable = R.drawable.ic_color_kitchen;
        else if(name.contains("Leather Goods"))
            drawable = R.drawable.ic_color_jacket_bag;
        else if(name.contains("LP Gas"))
            drawable = R.drawable.ic_color_gas;
        else if(name.contains("Machineries"))
            drawable = R.drawable.ic_color_sewing_machine;
        else if(name.contains("Women"))
            drawable = R.drawable.female_fashion;
        else if(name.toLowerCase().contains("men"))
            drawable = R.drawable.men_fashion;
        else if(name.contains("Paints"))
            drawable = R.drawable.ic_color_paint_bucket;
        else if(name.contains("Pet & Poultry Supplies"))
            drawable = R.drawable.ic_color_dog;
        else if(name.contains("Plastic made Products"))
            drawable = R.drawable.ic_color_bucket;
        else if(name.contains("Property"))
            drawable = R.drawable.ic_color_building;
        else if(name.contains("Service"))
            drawable = R.drawable.ic_color_maintenance;
        else if(name.contains("Shoes"))
            drawable = R.drawable.ic_color_new_shoes2;
        else if(name.contains("Sports"))
            drawable = R.drawable.ic_color_sports;
        else if(name.contains("Stationeries"))
            drawable = R.drawable.ic_color_pot;
        else if(name.contains("Vehicles & Parts"))
            drawable = R.drawable.ic_color_vehicles;
        else if(name.contains("Voucher"))
            drawable = 0;
        else if(name.contains("Watch & Clock"))
            drawable = R.drawable.ic_color_wathces;
        else
            drawable = 0;


        return drawable;

    }


}