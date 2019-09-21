package bd.com.evaly.evalyshop.activity.newsfeed.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.newsfeed.NewsfeedPendingFragment;
import bd.com.evaly.evalyshop.models.newsfeed.NewsfeedItem;
import bd.com.evaly.evalyshop.util.Utils;


public class NewsfeedPendingAdapter extends RecyclerView.Adapter<NewsfeedPendingAdapter.MyViewHolder>{

    ArrayList<NewsfeedItem> itemsList;
    Context context;
    NewsfeedPendingFragment fragment;

    public NewsfeedPendingAdapter(ArrayList<NewsfeedItem> itemsList, Context context, NewsfeedPendingFragment fragment) {
        this.itemsList = itemsList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public NewsfeedPendingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_newsfeed_list_confirmation,viewGroup,false);
        return new NewsfeedPendingAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsfeedPendingAdapter.MyViewHolder myViewHolder, int i) {


        myViewHolder.userNameView.setText(itemsList.get(i).getAuthorFullName());

        if (itemsList.get(i).getAuthorFullName().trim().equals(""))
            myViewHolder.userNameView.setText("User");

        myViewHolder.timeView.setText(Utils.getTimeAgo(Utils.formattedDateFromStringTimestamp("yyyy-MM-dd'T'HH:mm:ss.SSS","hh:mm aa - d',' MMMM", itemsList.get(i).getUpdatedAt())));

        myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(itemsList.get(i).getBody(), 180, "... <b>Show more</b>")));

        myViewHolder.statusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (myViewHolder.statusView.getText().toString().contains("Show more"))
                    myViewHolder.statusView.setText(itemsList.get(i).getBody());
                else
                    myViewHolder.statusView.setText(Html.fromHtml(Utils.truncateText(itemsList.get(i).getBody(), 180, "... <b>Show more</b>")));


            }
        });

        Glide.with(context)
                .load(itemsList.get(i).getAuthorImage())
                .fitCenter()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(200, 200))
                .placeholder(R.drawable.user_image)
                .into(myViewHolder.userImage);

        String postImage = itemsList.get(i).getAttachment();

        if (postImage.equals("null")){

            myViewHolder.postImage.setVisibility(View.GONE);

        } else {

            myViewHolder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(itemsList.get(i).getAttachmentCompressed())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .centerCrop()
                    .fitCenter()
                    .apply(new RequestOptions().override(900, 900))
                    .into(myViewHolder.postImage);
        }

    }


    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView userNameView, timeView, statusView, likeCountView, commentCountView;
        ImageView userImage, likeIcon, commentIcon, menuIcon, postImage;
        View view;
        LinearLayout likeHolder, commentHolder;
        public MyViewHolder(final View itemView) {
            super(itemView);

            userNameView = itemView.findViewById(R.id.user_name);
            timeView = itemView.findViewById(R.id.date);
            statusView = itemView.findViewById(R.id.text);


            userImage = itemView.findViewById(R.id.picture);
            postImage = itemView.findViewById(R.id.postImage);




            view = itemView;
        }
    }





}
