package bd.com.evaly.evalyshop.ui.reviews.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.reviews.ReviewItem;
import bd.com.evaly.evalyshop.util.Utils;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder>{

    ArrayList<ReviewItem> itemList;
    Context context;

    public ReviewsAdapter(ArrayList<ReviewItem> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_review,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.name.setText(itemList.get(i).getUser_name());

        try {
            myViewHolder.time.setText(Utils.formattedDateFromString("","hh:mm aa - d',' MMMM", itemList.get(i).getTime()));
        } catch (Exception e) {
            myViewHolder.time.setText(Utils.getTimeAgo(Integer.parseInt(itemList.get(i).getTime()) * 1000));
        }

        myViewHolder.reviewText.setText(itemList.get(i).getRating_text());

        if (itemList.get(i).getRating_text().equals("null"))
            myViewHolder.reviewText.setVisibility(View.GONE);

        myViewHolder.ratingBar.setRating((float) itemList.get(i).getRating_value());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, time, reviewText;

        RatingBar ratingBar;

        View view;
        public MyViewHolder(final View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            time=itemView.findViewById(R.id.time);
            reviewText = itemView.findViewById(R.id.review_text);
            ratingBar = itemView.findViewById(R.id.ratingBar);

            view = itemView;
        }
    }

}
