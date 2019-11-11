package bd.com.evaly.evalyshop.activity.chat;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.RequestListViewHolder> {
    private List<Jid> list;
    private Activity context;
    private OnAcceptRejectListener listener;

    public RequestListAdapter(List<Jid> list, Activity context, OnAcceptRejectListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestListViewHolder(LayoutInflater.from(context).inflate(R.layout.requst_item_view, parent, false));
    }

    public interface OnAcceptRejectListener{
        void onRequestAccept(Jid jid);
        void onRequestReject(Jid jid);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestListViewHolder holder, int position) {
        Jid model = list.get(position);
//        Logger.d(list.size() + "     " + position);

        try {
            VCard vCard = AppController.getmService().xmpp.getUserDetails((EntityBareJid) model);

//            Logger.json(new Gson().toJson(vCard));
            if (vCard.getFirstName() == null || vCard.getLastName().equalsIgnoreCase("")) {
                if (vCard.getNickName() == null || vCard.getNickName().replaceAll("\\s+$", "").equals("")) {
                    holder.tvName.setText("Customer");
                } else {
                    holder.tvName.setText(vCard.getNickName());
                }
            } else {
                holder.tvName.setText(vCard.getFirstName() + " " + vCard.getLastName());
            }
            Glide.with(context)
                    .load(vCard.getField("URL"))
                    .apply(new RequestOptions().placeholder(R.drawable.user_image))
                    .into(holder.ivProfileImage);
//            vCardList.add(vCard);

        } catch (Exception e) {
//            holder.tvName.setText(model.getRoasterPresenceFrom().getLocalpartOrNull().toString());
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RequestListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage)
        CircleImageView ivProfileImage;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.llAccept)
        LinearLayout llAccept;
        @BindView(R.id.llReject)
        LinearLayout llReject;


        public RequestListViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
//            Logger.d(chatItemList.size()+"     ");
//            if (chatItemList.size() == ) {
//                listener.onAllDataLoaded(list);
//            }

            llAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRequestAccept(list.get(getLayoutPosition()));
                }
            });

            llReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRequestReject(list.get(getLayoutPosition()));
                }
            });
        }
    }
}
