package bd.com.evaly.evalyshop.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.orderDetails.OrderDetailsActivity;
import bd.com.evaly.evalyshop.models.Orders;
import bd.com.evaly.evalyshop.util.UserDetails;
import bd.com.evaly.evalyshop.util.Utils;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder>{

    ArrayList<Orders> orders;
    Context context;
    UserDetails userDetails;

    public OrderAdapter(Context context,ArrayList<Orders> orders) {
        this.context=context;
        this.orders = orders;
        userDetails=new UserDetails(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order_list,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.orderID.setText(orders.get(i).getInvoiceNumber());


        try {

            myViewHolder.date.setText(Utils.formattedDateFromString("", "d MMM, hh:mm aa", orders.get(i).getTime()));

        } catch (Exception e) {

            String str = orders.get(i).getTime();
            String s[] = str.split("T");
            myViewHolder.date.setText(s[0]);
        }


        String orderStatus = orders.get(i).getStatus();
        String paymentStatus = orders.get(i).getPaymentStatus();



        try {

            if(orderStatus.toLowerCase().equals("cancel")){
                myViewHolder.status.setText("Cancelled");
            }else{
                myViewHolder.status.setText(Utils.toFirstCharUpperAll(orderStatus));
            }

        }catch (Exception e){

            myViewHolder.status.setText(orderStatus);

        }


        try {

            myViewHolder.paymentStatus.setText(Utils.toFirstCharUpperAll(paymentStatus));

        }catch (Exception e){

            myViewHolder.paymentStatus.setText(paymentStatus);

        }



        if (paymentStatus.toLowerCase().equals("paid")) {
            myViewHolder.paymentStatus.setBackgroundColor(Color.parseColor("#33d274"));

            myViewHolder.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        }
        else if (paymentStatus.toLowerCase().equals("unpaid")) {
            myViewHolder.paymentStatus.setBackgroundColor(Color.parseColor("#f0ac4e"));

            myViewHolder.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        }
        else if (paymentStatus.toLowerCase().equals("partial")) {
            myViewHolder.paymentStatus.setBackgroundColor(Color.parseColor("#009688"));

            myViewHolder.paymentStatus.setTextColor(Color.parseColor("#ffffff"));
        }
        else if(paymentStatus.toLowerCase().equals("refunded")) {
            myViewHolder.paymentStatus.setTextColor(Color.parseColor("#333333"));
            myViewHolder.paymentStatus.setBackgroundColor(Color.parseColor("#eeeeee"));
            //#33d274
        }



        if(orderStatus.toLowerCase().equals("cancel")){
            myViewHolder.status.setBackgroundColor(Color.parseColor("#d9534f"));
        }else if(orderStatus.toLowerCase().equals("delivered")){

            myViewHolder.status.setBackgroundColor(Color.parseColor("#4eb950"));

        }else if(orderStatus.toLowerCase().equals("pending")){

            myViewHolder.status.setBackgroundColor(Color.parseColor("#e79e03"));

        }else if(orderStatus.toLowerCase().equals("confirmed")){

            myViewHolder.status.setBackgroundColor(Color.parseColor("#7abcf8"));

        }else if(orderStatus.toLowerCase().equals("shipped")){

            myViewHolder.status.setBackgroundColor(Color.parseColor("#db9803"));

        }else if(orderStatus.toLowerCase().equals("picked")){

            myViewHolder.status.setBackgroundColor(Color.parseColor("#3698db"));

        }else if(orderStatus.toLowerCase().equals("processing")){

            myViewHolder.status.setBackgroundColor(Color.parseColor("#5ac1de"));

        }

       // getOrderImage(myViewHolder.orderImage,myViewHolder.orderID.getText().toString());


        myViewHolder.phone.setText(userDetails.getPhone());
        myViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent intent = new Intent(context, OrderDetailsActivity.class);

                    intent.putExtra("orderID", myViewHolder.orderID.getText().toString());
                    context.startActivity(intent);
                } catch (Exception e){

                    Intent intent = new Intent(context, OrderDetailsActivity.class);
                    intent.putExtra("orderID",myViewHolder.orderID.getText().toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView orderID,date,phone,status,paymentStatus;
        //ImageView orderImage;
        View view;
        public MyViewHolder(final View itemView) {
            super(itemView);
            orderID=itemView.findViewById(R.id.orderID);
            status=itemView.findViewById(R.id.status);
            paymentStatus = itemView.findViewById(R.id.paymentStatus);
            phone=itemView.findViewById(R.id.phone);
            date=itemView.findViewById(R.id.date);
           // orderImage=itemView.findViewById(R.id.order_image);
            view = itemView;
        }
    }

//    public void getOrderImage(ImageView iv,String orderID){
//        String url="https://api-prod.evaly.com.bd/api/order/"+orderID+"/";
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d("order_details",response.toString());
//                try {
//                    JSONArray jsonArray = response.getJSONArray("items");
//                    JSONObject ob = jsonArray.getJSONObject(0);
//                    Glide.with(context).
//                            load(ob.getJSONObject("shop_product_item").getJSONObject("product_item").getJSONObject("product").getString("thumbnail"))
//                            .apply(new RequestOptions().override(300, 300))
//                            .into(iv);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("onErrorResponse", error.toString());
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + userDetails.getToken()); // headers.put("Host", "api-prod.evaly.com.bd");                 headers.put("Content-Type", "application/json");                 headers.put("Origin", "https://evaly.com.bd");                 headers.put("Referer", "https://evaly.com.bd/");                 headers.put("User-Agent", userAgent);
//                return headers;
//            }
//        };
//        RequestQueue queue= Volley.newRequestQueue(context);
//        request.setRetryPolicy(new RetryPolicy() {
//            @Override
//            public int getCurrentTimeout() {
//                return 50000;
//            }
//
//            @Override
//            public int getCurrentRetryCount() {
//                return 50000;
//            }
//
//            @Override
//            public void retry(VolleyError error) throws VolleyError {
//
//            }
//        });
//        queue.add(request);
//    }
}
