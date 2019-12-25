package bd.com.evaly.evalyshop.ui.chat;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.RoasterModel;
import bd.com.evaly.evalyshop.util.Constants;
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
//        Logger.d(model.imageUrl+"     ======");


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
            if (model.name == null || model.name.equals("")) {
                ChatItem chatItem = new Gson().fromJson(model.lastMessage, ChatItem.class);
                if (model.nick_name == null || model.nick_name.replaceAll("\\s+$", "").equals("")) {
                    if (chatItem.getSender().contains(CredentialManager.getUserName())){
                        if (chatItem.getReceiver_name() == null || chatItem.getReceiver_name().trim().isEmpty()){
                            holder.tvName.setText("Evaly User");
                        }else {
                            holder.tvName.setText(chatItem.getReceiver_name());
                        }
                    } else {
                        if (chatItem.getName() == null || chatItem.getName().trim().isEmpty()){
                            holder.tvName.setText("Evaly User");
                        }else {
                            holder.tvName.setText(chatItem.getName());
                        }
                    }

                } else {
                    holder.tvName.setText(model.nick_name);
                }
            } else {
                holder.tvName.setText(model.name);
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


//            if (model.imageUrl != null && !model.imageUrl.equalsIgnoreCase("")) {
//
//                Glide.with(context)
//                        .load(model.imageUrl)
//                        .apply(new RequestOptions().placeholder(R.drawable.user_image))
//                        .into(holder.ivProfileImage);
//            } else {
//                StringBuilder initials = new StringBuilder();
//                for (String s : model.name.split(" ")) {
////            Logger.d(s);
//                    if (!s.trim().isEmpty()) {
//                        if (initials.length()<2){
//                            initials.append(s.charAt(0));
//                        }
//                    }
//                }
//                TextDrawable drawable = TextDrawable.builder()
//                        .beginConfig()
//                        .width(60)  // width in px
//                        .height(60) // height in px
//                        .endConfig()
//                        .buildRect(initials.toString().toUpperCase(), context.getResources().getColor(R.color.evaly_color));
//
//                holder.ivProfileImage.setImageDrawable(drawable);
//            }
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
                    } else if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_PRODUCT)) {
                        holder.tvBody.setText("You: Share a product");
                    }else if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_FEED)) {
                        holder.tvBody.setText("You: Share a newsfeed story");
                    } else {
                        holder.tvBody.setText("You: " + chatItem.getChat());
                    }

                } else {
                    if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_IMAGE)) {
                        holder.tvBody.setText("Sent an image");
                    } else if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_PRODUCT)) {
                        holder.tvBody.setText("Share a product");
                    } else if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_FEED)) {
                        holder.tvBody.setText("Share a newsfeed story");
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
        @BindView(R.id.tvShortName)
        TextView tvShortName;


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
