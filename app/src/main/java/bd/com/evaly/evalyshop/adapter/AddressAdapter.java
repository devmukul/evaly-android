package bd.com.evaly.evalyshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.util.UserDetails;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder>{

    Context context;
    ArrayList<String> address;
    ArrayList<Integer> addressID;
    UserDetails userDetails;
    EditText addressET;
    String tempAddress="";
    Activity activity;
    AlertDialog alertDialog;
    int index=0;
    RequestQueue queue;
    String userAgent;

    public AddressAdapter(Context context, ArrayList<String> address, ArrayList<Integer> addressID, Activity activity) {
        this.context = context;
        this.address = address;
        this.addressID=addressID;
        this.activity=activity;
        userDetails=new UserDetails(context);
        queue= Volley.newRequestQueue(context);

        userAgent = new WebView(context).getSettings().getUserAgentString();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_address,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.address.setText(address.get(i));
        myViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempAddress=myViewHolder.address.getText().toString();
                index=address.indexOf(tempAddress);
                addressDialog(myViewHolder.address,myViewHolder.address.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return address.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView address;
        ImageView trash;
        View view;

        public MyViewHolder(final View itemView) {
            super(itemView);
            address=itemView.findViewById(R.id.address);
            trash=itemView.findViewById(R.id.trash);
            view = itemView;
        }
    }

    public void getUserData(String str){
        String url="https://api-prod.evaly.com.bd/api/user/detail/"+userDetails.getUserName()+"/";
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("key", "value");
        } catch (Exception e) {
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse", response.toString());
                try {
                    JSONObject userJson = response;
                    JSONArray userInfo = userJson.getJSONArray("addresses");
                    JSONObject ob=new JSONObject();
                    ob.put("address",str);
                    ob.put("id",addressID.get(index));
                    for(int i=0;i<userInfo.length();i++){
                        if(userInfo.getJSONObject(i).getString("address").equals(tempAddress)){
                            userInfo.put(i,ob);
                            break;
                        }
                    }
                    setUserData(userJson);
                    Log.d("json user info", userJson.toString());
                    JSONArray addressArray=response.getJSONArray("addresses");
                    //JSONObject addressOB=response.getJSONObject("addresses");
                    if(addressArray.length()!=0){
                        String addressStr="",addressIDStr="";
                        for(int i=0;i<addressArray.length();i++){
                            JSONObject addressOB=addressArray.getJSONObject(i);
                            addressStr+=addressOB.get("address")+"::";
                            addressIDStr+=addressOB.get("id")+"::";
                        }
                        userDetails.setAddresses(addressStr);
                        userDetails.setAddressID(addressIDStr);
                        userDetails.setJsonAddress(addressArray.toString());
                    }else{
                        userDetails.setAddresses("");
                        userDetails.setAddressID("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                headers.put("Authorization", CredentialManager.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                return headers;
            }
        };
        // RequestQueue queue= Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void setUserData(JSONObject payload){
        String url="https://api-prod.evaly.com.bd/api/user/detail/"+userDetails.getUserName()+"/";
        Log.d("json user info url", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, payload, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // alert.hideDialog();
                Log.d("json user info response", response.toString());
                Toast.makeText(context,"Address updated successfully", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
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
                headers.put("Authorization", CredentialManager.getToken());
                // headers.put("Host", "api-prod.evaly.com.bd");
                headers.put("Content-Type", "application/json");
                headers.put("Origin", "https://evaly.com.bd");
                headers.put("Referer", "https://evaly.com.bd/");
                headers.put("User-Agent", userAgent);
                // headers.put("Content-Length", data.length()+"");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        // RequestQueue queue= Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void addressDialog(TextView tv,String str){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.item_edit_address, null);
        dialogBuilder.setView(dialogView);
        addressET = dialogView.findViewById(R.id.addressET);
        addressET.setText(str);
        Button addressAdd=dialogView.findViewById(R.id.addressAdd);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
        addressAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText(addressET.getText().toString());
                getUserData(addressET.getText().toString());
            }
        });
    }
}
