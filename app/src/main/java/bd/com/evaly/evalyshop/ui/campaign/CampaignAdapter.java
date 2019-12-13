package bd.com.evaly.evalyshop.ui.campaign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;

public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.ViewHolder>{

    private List<CampaignItem> itemList;
    private Context context;
    private CampaignListener listener;



    public CampaignAdapter(Context context, List<CampaignItem> items, CampaignListener listener){
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

        holder.tvTitle.setText(itemList.get(position).getName());

        holder.itemView.setOnClickListener(view -> listener.onItemClick(itemList.get(position)));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView imageView;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;

            tvTitle = itemView.findViewById(R.id.tvTitle);
            imageView = itemView.findViewById(R.id.image);

        }
    }

}
