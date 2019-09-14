package bd.com.evaly.evalyshop.activity.newsfeed;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.newsfeed.adapters.NewsfeedAdapter;
import bd.com.evaly.evalyshop.activity.newsfeed.models.NewsfeedItem;
import bd.com.evaly.evalyshop.models.Notifications;
import bd.com.evaly.evalyshop.util.UrlUtils;
import bd.com.evaly.evalyshop.util.UserDetails;

public class NewsfeedFragment extends Fragment {

    private String type;
    private RecyclerView recyclerView;
    private NewsfeedAdapter adapter;
    private ArrayList<NewsfeedItem> itemsList = new ArrayList<>();
    private Context context;
    private NewsfeedActivity activity;
    private UserDetails userDetails;

    public NewsfeedFragment() {
        // Required empty public constructor
    }

    public static NewsfeedFragment newInstance(String type) {
        NewsfeedFragment fragment = new NewsfeedFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        activity = (NewsfeedActivity) getActivity();

        if (getArguments() != null) {
            type = getArguments().getString("type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        userDetails=new UserDetails(context);

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new NewsfeedAdapter(itemsList, context);
        recyclerView.setAdapter(adapter);




    }






    public void getPosts(){
        String url= UrlUtils.BASE_URL_NEWSFEED+"posts";

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("type", type);
        } catch (Exception e) {
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("json response", response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    if(jsonArray.length()==0){
                        not.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else{
                        not.setVisibility(View.GONE);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject ob = jsonArray.getJSONObject(i);

                            Notifications item = new Notifications();
                            item.setId(ob.getString("id"));
                            item.setImageURL(ob.getString("thumb_url"));
                            item.setMessage(ob.getString("message"));
                            item.setTime(ob.getString("created_at"));
                            item.setContent_type(ob.getString("content_type"));
                            item.setContent_url(ob.getString("content_url"));
                            notifications.add(item);

                            adapter.notifyItemInserted(notifications.size());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                markAsRead();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + userDetails.getToken());
                return headers;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(this);
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(request);
    }


}
