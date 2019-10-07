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
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.RoasterModel;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private List<RoasterModel> list;
    private Activity context;
    private RecyclerViewOnItemClickListenerChat listener;
    private XMPPHandler xmppHandler;
    List<VCard> vCardList;
    public static List<ChatItem> chatItemList;

    public ChatListAdapter(List<RoasterModel> list, Activity context, RecyclerViewOnItemClickListenerChat listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
        xmppHandler = AppController.getmService().xmpp;
        vCardList = new ArrayList<>();
        chatItemList = new ArrayList<>();
    }

    public interface RecyclerViewOnItemClickListenerChat {
        void onItemClicked(VCard object, int position, RoasterModel roasterModel);
        void onAllDataLoaded(List<RoasterModel> roasterModelList);
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatListViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        RoasterModel model = list.get(position);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                VCard vCard = xmppHandler.getUserDetails(model.getRoasterEntryUser().asEntityBareJidIfPossible());
                Logger.d(new Gson().toJson(vCard));
                try {
                    if (vCard.getNickName() == null || vCard.getNickName().replaceAll("\\s+$", "").equals("")) {
                        if (vCard.getFirstName() == null){
                            holder.tvName.setText("Customer");
                        }else {
                            holder.tvName.setText(vCard.getFirstName() + " " + vCard.getLastName());
                        }
                    } else if (vCard.getNickName().trim().replaceAll("\\s+$", "").equals("")) {
                        holder.tvName.setText("Customer");
                    } else {
                        holder.tvName.setText(vCard.getNickName());
                    }
                    Glide.with(context)
                            .load(vCard.getField("URL"))
                            .apply(new RequestOptions().placeholder(R.drawable.user_image))
                            .into(holder.ivProfileImage);
                    vCardList.add(vCard);
                    if (model.getStatus() == Constants.PRESENCE_MODE_AVAILABLE_INT) {
                        holder.llOnlineStatus.setVisibility(View.VISIBLE);
                    } else {
                        holder.llOnlineStatus.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    holder.tvName.setText(model.getRoasterPresenceFrom().getLocalpartOrNull().toString());
                    e.printStackTrace();
                }

                ChatItem chatItem = null;
                try {
                    chatItem = xmppHandler.getLastMessage(vCard.getFrom());
                    list.get(position).setTime(chatItem.getLognTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (chatItem != null) {
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
                } else {
                    holder.tvBody.setText("Say hi");
                    holder.tvTime.setText("");
                }
            }
        });
    }

    public List<RoasterModel> getList(){
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
                    listener.onItemClicked(vCardList.get(getLayoutPosition()), getLayoutPosition(), list.get(getLayoutPosition()));
                }
            });
        }
    }
}
