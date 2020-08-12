package bd.com.evaly.evalyshop.ui.campaign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.CampaignListener;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.util.Utils;

public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.ViewHolder> {

    private List<CampaignItem> itemList;
    private Context context;
    private CampaignListener listener;


    public CampaignAdapter(Context context, List<CampaignItem> items, CampaignListener listener) {
        this.context = context;
        this.itemList = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_campaign, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CampaignAdapter.ViewHolder holder, int position) {

        CampaignItem model = itemList.get(position);

        holder.tvTitle.setText(model.getName());
        holder.itemView.setOnClickListener(view -> listener.onItemClick(model));

        Glide.with(holder.itemView.getContext())
                .load(model.getBannerImage())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.bg_fafafa_round)
                .into(holder.ivCover);

        Date startDate = Utils.getCampaignDate( model.getStartDate());
        Date endDate = Utils.getCampaignDate( model.getEndDate());
        Date currentDate = Calendar.getInstance().getTime();


        if (currentDate.after(startDate) && currentDate.before(endDate)) {
            holder.tvStatus.setText("Live Now");
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.btn_live_now_red));
        } else if (currentDate.after(endDate)){
            holder.tvStatus.setText("Expired");
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.btn_campaign_expired));
        } else {
            holder.tvStatus.setText("Live on " + Utils.getFormatedCampaignDate("", "d MMM hh:mm aa", model.getStartDate()));
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.btn_pending_bg));
        }


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivCover;
        TextView tvStatus;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;

            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivCover = itemView.findViewById(R.id.ivCover);
            tvStatus = itemView.findViewById(R.id.tvStatus);

        }
    }

}
