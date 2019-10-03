package bd.com.evaly.evalyshop.activity.chat;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.VCardObject;
import bd.com.evaly.evalyshop.util.RecyclerViewItemDecorator;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetailsAdapter extends RecyclerView.Adapter<ChatDetailsAdapter.ChatDetailsViewHolder> {

    private Activity context;
    private VCardObject vCard;
    List<ChatItem> chatItemList;

    public ChatDetailsAdapter(List<ChatItem> chatItemList, Activity context, VCardObject vCard) {
        this.context = context;
        this.vCard = vCard;
        this.chatItemList = chatItemList;
    }

    @NonNull
    @Override
    public ChatDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatDetailsViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_details_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatDetailsViewHolder holder, int position) {
        ChatItem chatItem = chatItemList.get(position);
//        Logger.d(vCard.getJid() +"  =======   "+ chatItem.getSender().toString());
        if (!chatItem.getSender().toString().contains(vCard.getJid().toString())) {
//            Logger.d("+++++++");
            holder.ivProfile.setVisibility(View.INVISIBLE);
            holder.tvMessage.setBackgroundResource(R.drawable.self_chat_back);
            holder.tvMessage.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvMessage.setText(chatItem.getChat());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.RIGHT;
            params.setMargins(getOriginalSize(62), getOriginalSize(16), 0, 0);
            holder.tvMessage.setGravity(Gravity.RIGHT);
            holder.tvMessage.setLayoutParams(params);
        } else {
//            Logger.d("----------");
            holder.ivProfile.setVisibility(View.VISIBLE);
            holder.tvMessage.setBackgroundResource(R.drawable.other_chat_back);
            holder.tvMessage.setTextColor(context.getResources().getColor(R.color.light_black));
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            holder.tvMessage.setText(chatItem.getChat());
            params.gravity = Gravity.LEFT;
            params.setMargins(getOriginalSize(52), getOriginalSize(16), getOriginalSize(60), 0);
            holder.tvMessage.setLayoutParams(params);
            holder.tvMessage.setGravity(Gravity.LEFT);
            Glide.with(context)
                    .load(vCard.getUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.user_image))
                    .into(holder.ivProfile);
        }

    }

    private int getOriginalSize(int val) {
        return RecyclerViewItemDecorator.dpToPx(context, val);
    }

    @Override
    public int getItemCount() {
        return chatItemList.size();
    }

    class ChatDetailsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivProfile)
        CircleImageView ivProfile;
        @BindView(R.id.tvMessage)
        TextView tvMessage;
        @BindView(R.id.tvSeen)
        TextView tvSeen;
        @BindView(R.id.tvChatTime)
        TextView tvChatTime;
        @BindView(R.id.llContainer)
        LinearLayout llContainer;

        public ChatDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tvMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    tvSeen.setVisibility(View.VISIBLE);
//                    tvChatTime.setVisibility(View.VISIBLE);
//                    if (position % 2 == 0) {
//                        holder.tvMessage.setBackgroundResource(R.drawable.self_chat_back_clicked);
//                    } else {
//                        holder.tvMessage.setBackgroundResource(R.drawable.other_chat_back_clicked);
//                    }
                }
            });
        }
    }
}
