package bd.com.evaly.evalyshop.activity.chat;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.ChatStateModel;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.models.xmpp.VCardObject;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvChatDetails)
    RecyclerView rvChatDetails;
    @BindView(R.id.etCommentsBox)
    EditText etCommentsBox;
    @BindView(R.id.ivSend)
    ImageView ivSend;
    @BindView(R.id.tvTyping)
    TextView tvTyping;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvOnlineStatus)
    TextView tvOnlineStatus;
    @BindView(R.id.llTyping)
    LinearLayout llTyping;
    @BindView(R.id.ivProfile)
    CircleImageView ivProfile;
    @BindView(R.id.ivProfileImage)
    CircleImageView ivProfileImage;
    @BindView(R.id.llOnlineStatus)
    LinearLayout llOnlineStatus;
    @BindView(R.id.ivBack)
    ImageView ivBack;

    private ChatDetailsAdapter adapter;
    private VCardObject vCard;
    private VCard mVCard;

    AppController mChatApp = AppController.getInstance();

    XMPPEventReceiver xmppEventReceiver;
    private XMPPHandler xmppHandler;
    private boolean isLoading, isLastPage;

//    LinearLayout.LayoutParams layoutParams;

    String messageId = null;

    private List<ChatItem> chatItemList;

    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    final Handler mHandler = new Handler();

//    private Runnable input_finish_checker = new Runnable() {
//        public void run() {
//            if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
//                // TODO: do what you need here
//                // ............
//                // ............
//                Logger.d("+++++++++++++++");
////                DoStaff();
//            }
//        }
//    };

    public XmppCustomEventListener xmppCustomEventListener = new XmppCustomEventListener() {

        //Event Listeners
        public void onNewMessageReceived(ChatItem chatItem) {
            Logger.d(chatItem.getChat());
            chatItem.setIsMine(false);

            Logger.d(mVCard.getFrom().toString());
            Logger.d(new Gson().toJson(chatItem));
            //                if( AppController.getmService().xmpp.checkSender(mVCard.getFrom(), JidCreate.bareFrom(chatItem.getSender()))) {
            chatItemList.add(chatItem);
            adapter.notifyDataSetChanged();
            rvChatDetails.smoothScrollToPosition(adapter.getItemCount() - 1);

//                }
        }

        //On User Presence Changed
        public void onPresenceChanged(PresenceModel presenceModel) {
//            Logger.d(presenceModel.getStatus());
            String presence = Utils.getStatusMode(presenceModel.getUserStatus());
            if (presenceModel.getUserStatus() == 1) {
                tvOnlineStatus.setText(presence);
                llOnlineStatus.setVisibility(View.VISIBLE);
                tvOnlineStatus.setVisibility(View.VISIBLE);
            } else {
                tvOnlineStatus.setText(Utils.getTimeAgoOnline(presenceModel.getTime()));
                llOnlineStatus.setVisibility(View.GONE);
            }

        }

        //On Chat Status Changed
        public void onChatStateChanged(ChatStateModel chatStateModel) {
//            Logger.d(chatStateModel.getChatState());
            String chatStatus = Utils.getChatMode(chatStateModel.getChatState());

            if (chatStateModel.getChatState() == ChatState.composing) {
//                layoutParams.setMargins(0, 0, 0, 50);
//                llRecyclerView.setLayoutParams(layoutParams);

//                final int newLeftMargin = 40;
//                Animation a = new Animation() {
//
//                    @Override
//                    protected void applyTransformation(float interpolatedTime, Transformation t) {
//                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rvChatDetails.getLayoutParams();
//                        params.bottomMargin = (int)(newLeftMargin * interpolatedTime);
//                        rvChatDetails.setLayoutParams(params);
//                    }
//                };
//                a.setDuration(300); // in ms
//                rvChatDetails.startAnimation(a);

                Glide.with(ChatDetailsActivity.this)
                        .load(vCard.getUrl())
                        .apply(new RequestOptions().placeholder(R.drawable.user_image))
                        .into(ivProfile);

                tvTyping.setText(vCard.getName() + " is typing");
                llTyping.setVisibility(View.VISIBLE);

            } else {
//                layoutParams.setMargins(0, 0, 0, 0);
//                llRecyclerView.setLayoutParams(layoutParams);
                llTyping.setVisibility(View.GONE);
            }

            try {
                if (AppController.getmService().xmpp.checkSender(mVCard.getFrom(), JidCreate.bareFrom(chatStateModel.getUser()))) {
//                    chatStatusTv.setText(chatStatus);
                    Logger.d(chatStatus);
                }
            } catch (XmppStringprepException e) {
                e.printStackTrace();
            }
        }

        public void onSubscriptionRequest(final String fromUserID) {
            Log.e("lol", "New request - " + fromUserID);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        ButterKnife.bind(this);

        vCard = (VCardObject) getIntent().getSerializableExtra("vcard");

        setSupportActionBar(toolbar);
        tvName.setText(vCard.getName());

        if (vCard.getStatus() == 1){
            tvOnlineStatus.setVisibility(View.VISIBLE);
            tvOnlineStatus.setText("Online");
            llOnlineStatus.setVisibility(View.VISIBLE);
        }else {
            tvOnlineStatus.setVisibility(View.GONE);
            llOnlineStatus.setVisibility(View.GONE);
        }

        Glide.with(this)
                .load(vCard.getUrl())
                .apply(new RequestOptions().placeholder(R.drawable.user_image))
                .into(ivProfileImage);

        chatItemList = new ArrayList<>();
        xmppHandler = AppController.getmService().xmpp;
        Logger.d(vCard.getJid());

        chatItemList = xmppHandler.getChatHistoryWithJID(vCard.getJid(), 10, false);

        mVCard = xmppHandler.getCurrentUserDetails();

        xmppEventReceiver = mChatApp.getEventReceiver();

        adapter = new ChatDetailsAdapter(chatItemList, this, vCard);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvChatDetails.setLayoutManager(layoutManager);
        rvChatDetails.setAdapter(adapter);
        if (adapter.getItemCount() > 0) {
            rvChatDetails.smoothScrollToPosition(adapter.getItemCount() - 1);
        }


//        layoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etCommentsBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                AppController.getmService().xmpp.updateChatStatus(vCard.getJid().asBareJid().toString(), ChatState.composing);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(userStoppedTyping, (Constants.PAUSE_THRESHOLD * 500)); // 1 second
            }
        });

        rvChatDetails.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int itemno = layoutManager.findFirstVisibleItemPosition();

//                Logger.d(itemno + " =======");
//                Logger.d(newState + " +++++++++");
                if (itemno == 0) {
                    if (!isLoading && !isLastPage) {
                        isLoading = true;
                        if (chatItemList.size() > 0) {
                            messageId = chatItemList.get(0).getMsgId();
                        }
                        Logger.d("UID======   "+messageId);
//                        addlisttop(xmppHandler.getChatHistoryWithPagination(vCard.getJid(), 10, messageId));
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

//                Logger.d(visibleItemCount + "     " + totalItemCount + "     " + firstVisibleItemPosition);
//                if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {

//                    Logger.d("LAST ITEM");
//                        loadMoreItems();
                }
//                }
            }
        });
    }

    Runnable userStoppedTyping = new Runnable() {

        @Override
        public void run() {

            //User haven't typed for PAUSE_THRESHOLD secs, mark chat status "Paused"
            AppController.getmService().xmpp.updateChatStatus(vCard.getJid().asBareJid().toString(), ChatState.paused);
        }
    };

    public void addlisttop(List<ChatItem> list) {
        if (list.size() < 10) {
            isLastPage = true;
        }
        for (int i = 0; i < list.size(); i++) {
            chatItemList.add(i, list.get(i));
            adapter.notifyItemInserted(i);
        }
        if (chatItemList.size() > 0) {
            messageId = chatItemList.get(0).getMsgId();
        }
        isLoading = false;
    }

    @OnClick(R.id.ivSend)
    void send() {

        if (!etCommentsBox.getText().toString().trim().isEmpty() && mVCard != null) {

            ChatItem chatItem = new ChatItem(etCommentsBox.getText().toString().trim(), mVCard.getFirstName() + " " + mVCard.getLastName(), mVCard.getField("URL"), mVCard.getNickName(), System.currentTimeMillis(), mVCard.getFrom().asBareJid().toString(), vCard.getJid().asBareJid().toString(), Constants.TYPE_TEXT, true);
            chatItem.setUid(CredentialManager.getUserName()+ System.currentTimeMillis());
            chatItemList.add(chatItem);
            Logger.d(new Gson().toJson(chatItem));
            adapter.notifyDataSetChanged();
            rvChatDetails.smoothScrollToPosition(adapter.getItemCount() - 1);
            try {
                etCommentsBox.setText("");
                xmppHandler.sendMessage(chatItem);
            } catch (SmackException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        xmppEventReceiver.setListener(xmppCustomEventListener);
    }

    @OnClick(R.id.ivBack)
    void back(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//        }
        return super.onOptionsItemSelected(item);
    }
}
