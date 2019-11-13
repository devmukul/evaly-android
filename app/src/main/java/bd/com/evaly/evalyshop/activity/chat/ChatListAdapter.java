package bd.com.evaly.evalyshop.activity.chat;

import android.app.Activity;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.RoasterModel;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private List<RosterTable> list;
    private Activity context;
    private RecyclerViewOnItemClickListenerChat listener;
    List<VCard> vCardList;
    public static List<ChatItem> chatItemList;
    private List<ChatListViewHolder> chatListViewHolderList;

    public ChatListAdapter(List<RosterTable> list, Activity context, RecyclerViewOnItemClickListenerChat listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
        vCardList = new ArrayList<>();
        chatItemList = new ArrayList<>();
        chatListViewHolderList = new ArrayList<>();
    }

    public interface RecyclerViewOnItemClickListenerChat {
        void onItemClicked(RosterTable roasterModel);

        void onAllDataLoaded(List<RoasterModel> roasterModelList);
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatListViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        RosterTable model = list.get(position);
//        Logger.d(list.size() + "     " + position);


        try {
//            if (model.rosterName == null) {
//                if (model.name != null ) {
//                    tvName.setText(rosterTable.name);
//                } else if (model.nick_name == null ) {
//                    tvName.setText("Customer");
//                } else {
//                    tvName.setText(model.nick_name);
//                }
//
//            } else {
//                tvName.setText(rosterTable.name);
//            }
            if (model.rosterName == null || model.rosterName.equals("")) {
                if (model.nick_name == null || model.nick_name.replaceAll("\\s+$", "").equals("")) {
                    holder.tvName.setText("Customer");
                } else {
                    holder.tvName.setText(model.nick_name);
                }
            } else {
                holder.tvName.setText(model.rosterName);
            }
            Glide.with(context)
                    .load(model.imageUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.user_image))
                    .into(holder.ivProfileImage);
//            vCardList.add(vCard);
            if (model.status == Constants.PRESENCE_MODE_AVAILABLE_INT) {
                holder.llOnlineStatus.setVisibility(View.VISIBLE);
            } else {
                holder.llOnlineStatus.setVisibility(View.GONE);
            }
            ChatItem chatItem = new Gson().fromJson(model.lastMessage, ChatItem.class);
            if (chatItem == null) {
                holder.tvBody.setText("Say hi");
                holder.tvTime.setText("");
            } else {
                if (chatItem.getSender().contains(CredentialManager.getUserName())) {
                    if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_IMAGE)) {
                        holder.tvBody.setText("You: Sent an image");
                    } else {
                        holder.tvBody.setText("You: " + chatItem.getChat());
                    }

                } else {
                    if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_IMAGE)) {
                        holder.tvBody.setText("Sent an image");
                    } else {
                        holder.tvBody.setText(chatItem.getChat());
                    }
                }
                holder.tvTime.setText(chatItem.getTime());

                if (model.unreadCount > 0) {
//                    Logger.d(model.unreadCount);
                    holder.tvUnreadCount.setText(String.valueOf(model.unreadCount));
                    holder.tvUnreadCount.setVisibility(View.VISIBLE);
                } else {
                    holder.tvUnreadCount.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
//            holder.tvName.setText(model.getRoasterPresenceFrom().getLocalpartOrNull().toString());
            e.printStackTrace();
        }

        chatListViewHolderList.add(holder);
    }

    public void loadUnreadMessage() {
        for (int i = 0; i < chatListViewHolderList.size(); i++) {

        }
    }

    public List<RosterTable> getList() {
        return list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ChatListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage)
        CircleImageView ivProfileImage;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvBody)
        TextView tvBody;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.llOnlineStatus)
        LinearLayout llOnlineStatus;
        @BindView(R.id.tvUnreadCount)
        TextView tvUnreadCount;


        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
//            Logger.d(chatItemList.size()+"     ");
//            if (chatItemList.size() == ) {
//                listener.onAllDataLoaded(list);
//            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClicked(list.get(getLayoutPosition()));
//                    unreadCount.set(getLayoutPosition(), 0);
                }
            });
        }
    }
}
