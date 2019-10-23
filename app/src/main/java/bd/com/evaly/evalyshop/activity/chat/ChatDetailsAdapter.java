package bd.com.evaly.evalyshop.activity.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.orhanobut.logger.Logger;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.ImagePreview;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.VCardObject;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.RecyclerViewItemDecorator;
import bd.com.evaly.evalyshop.util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Activity context;
    private RosterTable vCard;
    List<ChatItem> chatItemList;

    boolean received, sent;

    public ChatDetailsAdapter(List<ChatItem> chatItemList, Activity context, RosterTable vCard) {
        this.context = context;
        this.vCard = vCard;
        this.chatItemList = chatItemList;
    }

    @Override
    public int getItemViewType(int position) {
        ChatItem chatItem = chatItemList.get(position);
//        Logger.d(vCard.getJid() +"  =======   "+ chatItem.getSender().toString());
        if (!chatItem.getSender().toString().contains(vCard.id)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            sent = true;
            received = false;
            return new SentMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.sent_message_view, parent, false));
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            received = true;
            sent = false;
            return new ReceiveMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_details_view, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatItem chatItem = chatItemList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageViewHolder) holder).bind(chatItem);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceiveMessageViewHolder) holder).bind(chatItem);

        }
//        Logger.d(vCard.getJid() +"  =======   "+ chatItem.getSender().toString());

    }

    private int getOriginalSize(int val) {
        return RecyclerViewItemDecorator.dpToPx(context, val);
    }

    @Override
    public int getItemCount() {
        return chatItemList.size();
    }

    class SentMessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvMessage)
        TextView tvMessage;
        @BindView(R.id.tvSeen)
        TextView tvSeen;
        @BindView(R.id.tvChatTime)
        TextView tvChatTime;
        @BindView(R.id.llContainer)
        LinearLayout llContainer;
        @BindView(R.id.cardImage)
        CardView cardImage;
        @BindView(R.id.ivImage)
        ImageView ivImage;
        @BindView(R.id.imageProgress)
        ProgressBar imageProgress;
        @BindView(R.id.tvUnreadMessage)
        TextView tvUnreadMessage;
        boolean isShow = false;


        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tvMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isShow) {
//                        tvSeen.setVisibility(View.GONE);
                        tvChatTime.setVisibility(View.GONE);
                        tvMessage.setBackgroundResource(R.drawable.self_chat_back);

                        isShow = false;
                    } else {
//                        tvSeen.setVisibility(View.VISIBLE);
                        tvChatTime.setVisibility(View.VISIBLE);
                        tvMessage.setBackgroundResource(R.drawable.self_chat_back_clicked);
                        isShow = true;
                    }
//                    if (position % 2 == 0) {
//                        holder.tvMessage.setBackgroundResource(R.drawable.self_chat_back_clicked);
//                    } else {
//                        holder.tvMessage.setBackgroundResource(R.drawable.other_chat_back_clicked);
//                    }
                }
            });
        }

        void bind(ChatItem chatItem) {

            if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_IMAGE)) {
                tvMessage.setVisibility(View.GONE);
                imageProgress.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.VISIBLE);
                if (chatItem.getLarge_image() != null) {
                    ivImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ImagePreview.class);
                            intent.putExtra("image", chatItem.getLarge_image());
                            //Toast.makeText(context, imgURL, Toast.LENGTH_SHORT).show();
                            context.startActivity(intent);
                        }
                    });
                }
                if (chatItem.getChat() != null || !chatItem.getChat().equalsIgnoreCase("")) {
                    Glide.with(context)
                            .load(chatItem.getChat())
                            .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    imageProgress.setVisibility(View.GONE);
                                    Logger.d("=========");

                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    imageProgress.setVisibility(View.GONE);
//                                    Logger.d("=========");

                                    ivImage.setVisibility(View.VISIBLE);
                                    cardImage.setVisibility(View.VISIBLE);
                                    return false;
                                }
                            })
                            .into(ivImage);
                }
            } else {
                ivImage.setVisibility(View.GONE);
                imageProgress.setVisibility(View.GONE);
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(chatItem.getChat());
            }
            if (chatItem.isUnread()) {
                tvUnreadMessage.setVisibility(View.VISIBLE);
            } else {
                tvUnreadMessage.setVisibility(View.GONE);
            }
            tvChatTime.setText(Utils.getTimeAgo(chatItem.getLognTime()));
        }
    }

    class ReceiveMessageViewHolder extends RecyclerView.ViewHolder {

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
        @BindView(R.id.cardImage)
        CardView cardImage;
        @BindView(R.id.ivImage)
        ImageView ivImage;
        @BindView(R.id.imageProgress)
        ProgressBar imageProgress;
        @BindView(R.id.tvUnreadMessage)
        TextView tvUnreadMessage;

        boolean isShow = false;

        public ReceiveMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            tvMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isShow) {
//                        tvSeen.setVisibility(View.GONE);
                        tvChatTime.setVisibility(View.GONE);
                        tvMessage.setBackgroundResource(R.drawable.other_chat_back);
                        isShow = false;
                    } else {
//                        tvSeen.setVisibility(View.VISIBLE);
                        tvChatTime.setVisibility(View.VISIBLE);
                        tvMessage.setBackgroundResource(R.drawable.other_chat_back_clicked);
                        isShow = true;
                    }
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

        void bind(ChatItem chatItem) {
            if (getLayoutPosition() + 1 < chatItemList.size()) {
                if (chatItemList.get(getLayoutPosition()+1).getSender().contains(vCard.id)){
                    ivProfile.setVisibility(View.INVISIBLE);
                    tvMessage.setBackgroundResource(R.drawable.other_chat_back_round);
                }else {
                    ivProfile.setVisibility(View.VISIBLE);
                    tvMessage.setBackgroundResource(R.drawable.other_chat_back);

                }
            }
            if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_IMAGE)) {
                if (chatItem.getLarge_image() != null) {
                    ivImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ImagePreview.class);
                            intent.putExtra("image", chatItem.getLarge_image());
                            //Toast.makeText(context, imgURL, Toast.LENGTH_SHORT).show();
                            context.startActivity(intent);
                        }
                    });
                }
                tvMessage.setVisibility(View.GONE);
                imageProgress.setVisibility(View.VISIBLE);
                ivImage.setVisibility(View.VISIBLE);
                if (chatItem.getChat() != null || !chatItem.getChat().isEmpty()) {
                    ivImage.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(chatItem.getChat())
                            .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    imageProgress.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    imageProgress.setVisibility(View.GONE);
                                    ivImage.setVisibility(View.VISIBLE);
                                    cardImage.setVisibility(View.VISIBLE);
                                    return false;
                                }
                            })
                            .into(ivImage);
                }
            } else {
//                if (received){
//                    tvMessage.setBackgroundResource(R.drawable.other_chat_back_round);
//                }else {
//                    tvMessage.setBackgroundResource(R.drawable.other_chat_back);
//                }
                ivImage.setVisibility(View.GONE);
                imageProgress.setVisibility(View.GONE);
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(chatItem.getChat());
            }

            if (chatItem.isUnread()) {
                tvUnreadMessage.setVisibility(View.VISIBLE);
            } else {
                tvUnreadMessage.setVisibility(View.GONE);
            }

            tvMessage.setText(chatItem.getChat());
            Glide.with(context)
                    .load(vCard.imageUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.user_image))
                    .into(ivProfile);
        }
    }
}
