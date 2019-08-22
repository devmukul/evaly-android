package bd.com.evaly.evalyshop.adapter;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.fragment.VoucherMyListFragment;
import bd.com.evaly.evalyshop.util.SearchFilterItem;
import bd.com.evaly.evalyshop.util.SearchFilterItem;
import bd.com.evaly.evalyshop.util.Utils;

public class SearchFilterAdapter extends RecyclerView.Adapter<SearchFilterAdapter.MyViewHolder>{

    Context context;
    ArrayList<SearchFilterItem> SearchFilterItems;

    public SearchFilterAdapter(Context context, ArrayList<SearchFilterItem> SearchFilterItems) {
        this.context = context;
        this.SearchFilterItems = SearchFilterItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_filter,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {


        String typeString = "Categories";

        if (SearchFilterItems.get(i).getType().equals("brand_name"))
            typeString = "Brands";
        else if (SearchFilterItems.get(i).getType().equals("color"))
            typeString = "Colors";


        if (i == 0){
            myViewHolder.type.setVisibility(View.VISIBLE);
        }else {

            try {

                if (SearchFilterItems.get(i-1).getType().equals(SearchFilterItems.get(i).getType()))
                    myViewHolder.type.setVisibility(View.GONE);
                else
                    myViewHolder.type.setVisibility(View.VISIBLE);


            }catch (Exception e){

            }

        }



        myViewHolder.type.setText(typeString);
        myViewHolder.name.setText(SearchFilterItems.get(i).getName());
        myViewHolder.count.setText(SearchFilterItems.get(i).getCount() + "");


        myViewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SearchFilterItems.get(i).isSelected()) {
                    SearchFilterItems.get(i).setSelected(false);
                } else {
                    SearchFilterItems.get(i).setSelected(true);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return SearchFilterItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView type, count;
        CheckBox name;
        View view;


        public MyViewHolder(final View itemView) {
            super(itemView);
            type=itemView.findViewById(R.id.type);
            count=itemView.findViewById(R.id.count);
            name=itemView.findViewById(R.id.checkBox);

            view=itemView;
        }
    }
}
