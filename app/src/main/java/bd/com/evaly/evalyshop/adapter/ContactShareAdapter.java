package bd.com.evaly.evalyshop.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.RecyclerViewOnItemClickListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactShareAdapter extends RecyclerView.Adapter<ContactShareAdapter.ContactShareViewHolder> implements Filterable {

    private Activity context;
    private List<RosterTable> list;
    private List<RosterTable> listFiltered;
    private RecyclerViewOnItemClickListener listener;

    public ContactShareAdapter(Activity context, List<RosterTable> list, RecyclerViewOnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listFiltered = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactShareViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ContactShareViewHolder(LayoutInflater.from(context).inflate(R.layout.share_with_contact_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactShareViewHolder holder, int i) {
        RosterTable model = listFiltered.get(i);
        if (model.rosterName == null || model.rosterName.equals("")) {
            if (model.nick_name == null || model.nick_name.replaceAll("\\s+$", "").equals("")) {
                holder.tvName.setText("Evaly User");
            } else {
                holder.tvName.setText(model.nick_name);
            }
        } else {
            holder.tvName.setText(model.rosterName);
        }

        if (model.imageUrl == null || model.imageUrl.trim().isEmpty()) {
            StringBuilder initials = new StringBuilder();
            for (String s : holder.tvName.getText().toString().split(" ")) {
//            Logger.d(s);
                if (!s.trim().isEmpty()) {
                    if (initials.length() < 2) {
                        initials.append(s.charAt(0));
                    }
                }
            }
            holder.tvShortName.setVisibility(View.VISIBLE);
            holder.tvShortName.setText(initials.toString().toUpperCase());
            holder.ivProfileImage.setVisibility(View.GONE);
        } else {
            holder.ivProfileImage.setVisibility(View.VISIBLE);
            holder.tvShortName.setVisibility(View.GONE);
            Glide.with(context)
                    .load(model.imageUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.user_image))
                    .into(holder.ivProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return listFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFiltered = list;
                } else {
                    List<RosterTable> filteredList = new ArrayList<>();
                    for (RosterTable table : list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (table.name.toLowerCase().contains(charString.toLowerCase()) || table.id.toLowerCase().contains(charSequence)) {
                            filteredList.add(table);
                        }
                    }
                    listFiltered = filteredList;
                    Logger.d(listFiltered.size());
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listFiltered = ((List<RosterTable>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    class ContactShareViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvShortName)
        TextView tvShortName;
        @BindView(R.id.llSend)
        LinearLayout llSend;
        @BindView(R.id.ivProfileImage)
        CircleImageView ivProfileImage;

        public ContactShareViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            llSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRecyclerViewItemClicked(list.get(getLayoutPosition()));
                }
            });
        }
    }
}
