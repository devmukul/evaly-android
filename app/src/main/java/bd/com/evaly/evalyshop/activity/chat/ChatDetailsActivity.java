package bd.com.evaly.evalyshop.activity.chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.models.xmpp.ChatStateModel;
import bd.com.evaly.evalyshop.models.xmpp.PresenceModel;
import bd.com.evaly.evalyshop.models.xmpp.VCardObject;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.Utils;
import bd.com.evaly.evalyshop.util.ViewDialog;
import bd.com.evaly.evalyshop.viewmodel.ImageUploadView;
import bd.com.evaly.evalyshop.xmpp.XMPPEventReceiver;
import bd.com.evaly.evalyshop.xmpp.XMPPHandler;
import bd.com.evaly.evalyshop.xmpp.XMPPService;
import bd.com.evaly.evalyshop.xmpp.XmppCustomEventListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GALLERY = 1112;
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

    @BindView(R.id.emoji_btn)
    ImageView emojiBtn;





    private ChatDetailsAdapter adapter;
    private VCard mVCard;

    private ViewDialog dialog;

    AppController mChatApp = AppController.getInstance();

    XMPPEventReceiver xmppEventReceiver;
    private XMPPHandler xmppHandler;
    private boolean isLoading, isLastPage;

//    LinearLayout.LayoutParams layoutParams;

    String messageId = null;
    private RosterTable rosterTable;

    private List<ChatItem> chatItemList;
    LinearLayoutManager layoutManager;

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

        public void onConnected() {
            xmppHandler = AppController.getmService().xmpp;
        }

        public void onLoggedIn() {
            xmppHandler = AppController.getmService().xmpp;
            mVCard = xmppHandler.mVcard;
            loadMessage();
        }

        //Event Listeners
        public void onNewMessageReceived(ChatItem chatItem) {
            Logger.d(chatItem.getChat());
            chatItem.setIsMine(false);

            Logger.d(mVCard.getFrom().toString() + "    " + chatItem.getReceiver());
            Logger.d(new Gson().toJson(chatItem));
            //                if( AppController.getmService().xmpp.checkSender(mVCard.getFrom(), JidCreate.bareFrom(chatItem.getSender()))) {
            if (mVCard.getFrom().toString().contains(chatItem.getReceiver())) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppController.database.taskDao().updateLastMessage(new Gson().toJson(chatItem), chatItem.getLognTime(), rosterTable.id, 0);
                    }
                });
                chatItemList.add(chatItem);
                if (chatItemList.size() > 1) {
                    adapter.notifyItemRangeChanged(chatItemList.size()-2, chatItemList.size()-1);
                }else {
                    adapter.notifyItemInserted(chatItemList.size() - 1);
                }
                rvChatDetails.smoothScrollToPosition(adapter.getItemCount() - 1);
            } else {
//                AsyncTask.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        AppController.database.taskDao().updateLastMessage(new Gson().toJson(chatItem), chatItem.getLognTime(), chatItem.getSender(), 0);
//                    }
//                });
            }

        }

        public void onNewMessageSent(ChatItem chatItem) {
            Logger.d(new Gson().toJson(chatItem));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    AppController.database.taskDao().updateLastMessage(new Gson().toJson(chatItem), chatItem.getLognTime(), rosterTable.id, 0);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (chatItem.getMessageType().equalsIgnoreCase(Constants.TYPE_IMAGE)) {
                                    Logger.d("==========");
                                    chatItemList.remove(chatItemList.size() - 1);
                                    adapter.notifyDataSetChanged();
//                                    adapter.notifyItemRemoved(chatItemList.size()-1);
                                    Logger.d(chatItemList.size());
                                }
                                chatItemList.add(chatItem);
                                adapter.notifyItemInserted(chatItemList.size() - 1);
                                rvChatDetails.scrollToPosition(chatItemList.size() - 1);
                                Logger.d(chatItemList.size());

                            } catch (Exception e) {
                                Logger.d(e.getMessage());
                            }
                        }
                    });
                }
            });
        }

        //On User Presence Changed
        public void onPresenceChanged(PresenceModel presenceModel) {
//            Logger.d(presenceModel.getStatus());
            if (!presenceModel.getUser().contains(Constants.EVALY_NUMBER)) {
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

        }

        //On Chat Status Changed
        public void onChatStateChanged(ChatStateModel chatStateModel) {
            String chatStatus = Utils.getChatMode(chatStateModel.getChatState());

            if (chatStateModel.getUser().contains(rosterTable.id)) {
                if (chatStateModel.getChatState() == ChatState.composing) {

                    Glide.with(ChatDetailsActivity.this)
                            .load(rosterTable.imageUrl)
                            .apply(new RequestOptions().placeholder(R.drawable.user_image))
                            .into(ivProfile);


                    tvTyping.setText(tvName.getText().toString() + " is typing");
                    llTyping.setVisibility(View.VISIBLE);

                } else {
//                layoutParams.setMargins(0, 0, 0, 0);
//                llRecyclerView.setLayoutParams(layoutParams);
                    llTyping.setVisibility(View.GONE);
                }
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


        EmojiManager.install(new GoogleEmojiProvider());

        ButterKnife.bind(this);

        startXmppService();

        rosterTable = (RosterTable) getIntent().getSerializableExtra("roster");
        Logger.d(new Gson().toJson(rosterTable));

        dialog = new ViewDialog(ChatDetailsActivity.this);
        setSupportActionBar(toolbar);

        ViewGroup rootView = (ViewGroup) findViewById(R.id.main_activity_root_view);


        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(etCommentsBox);
        emojiPopup.toggle();




        if (rosterTable.rosterName == null) {
            if (rosterTable.name != null) {
                tvName.setText(rosterTable.name);
            } else if (rosterTable.nick_name == null) {
                tvName.setText("Customer");
            } else {
                tvName.setText(rosterTable.nick_name);
            }

        } else {
            if (rosterTable.rosterName.equals("")) {
                if (rosterTable.name != null) {
                    tvName.setText(rosterTable.name);
                } else if (rosterTable.nick_name == null) {
                    tvName.setText("Customer");
                } else {
                    tvName.setText(rosterTable.nick_name);
                }
            } else {
                tvName.setText(rosterTable.rosterName);
            }
        }

        if (rosterTable.status == 1) {
            tvOnlineStatus.setVisibility(View.VISIBLE);
            tvOnlineStatus.setText("Online");
            llOnlineStatus.setVisibility(View.VISIBLE);
        } else {
            tvOnlineStatus.setVisibility(View.GONE);
            llOnlineStatus.setVisibility(View.GONE);
        }

        Glide.with(this)
                .load(rosterTable.imageUrl)
                .apply(new RequestOptions().placeholder(R.drawable.user_image))
                .into(ivProfileImage);

        layoutManager = new LinearLayoutManager(ChatDetailsActivity.this);

        chatItemList = new ArrayList<>();
//        xmppHandler = AppController.getmService().xmpp;
//        Logger.d(vCard.getJid());

        mVCard = xmppHandler.mVcard;

        xmppEventReceiver = mChatApp.getEventReceiver();

        if (xmppHandler.isConnected()) {
            loadMessage();
        } else {
            startXmppService();
        }

//        Logger.d(rosterTable.unreadCount + "    ==========");

//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                new ChatHistoryAsyncTask().execute();
//            }
//        });

//        Logger.json(new Gson().toJson(chatItemList));


//        layoutParams =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etCommentsBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                AppController.getmService().xmpp.updateChatStatus(rosterTable.id, ChatState.composing);
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
                    Logger.d(isLastPage+"    "+ isLoading);
                    if (!isLoading && !isLastPage) {
                        isLoading = true;
                        if (chatItemList.size() > 0) {
                            messageId = chatItemList.get(0).getMessageId();
                        }
                        Logger.d("UID======   " + messageId);
                        if (messageId != null) {
                            try {
                                addlisttop(xmppHandler.getChatHistoryWithPagination(JidCreate.bareFrom(rosterTable.id), 15, messageId));
                            } catch (XmppStringprepException e) {
                                e.printStackTrace();
                            }
                        }
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

        try {
            if (rosterTable.unreadCount > 0) {
                ChatItem chatItem = new Gson().fromJson(rosterTable.lastMessage, ChatItem.class);
                xmppHandler.markAsRead(chatItem.getMessageId(), JidCreate.bareFrom(rosterTable.id), xmppHandler.mVcard.getFrom());
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        AppController.database.taskDao().updateUnreadCount(0, rosterTable.id);
                    }
                });
            }
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMessage() {
        dialog.showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    chatItemList = xmppHandler.getChatHistoryWithJID(JidCreate.bareFrom(rosterTable.id), 20, false);
                    if (rosterTable.unreadCount > 0) {
                        chatItemList.get(chatItemList.size() - rosterTable.unreadCount).setUnread(true);
                    }
                    Logger.json(new Gson().toJson(chatItemList));
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ChatDetailsAdapter(chatItemList, ChatDetailsActivity.this, rosterTable);
                        rvChatDetails.setLayoutManager(layoutManager);
                        rvChatDetails.setAdapter(adapter);
                        if (adapter.getItemCount() > 0) {
                            Logger.d("_+_+_+__+_+_+_+_+_+");
                            rvChatDetails.scrollToPosition(adapter.getItemCount() - 1);
                        }
                        dialog.hideDialog();
                    }
                });
            }
        }).start();
    }

    Runnable userStoppedTyping = new Runnable() {

        @Override
        public void run() {

            //User haven't typed for PAUSE_THRESHOLD secs, mark chat status "Paused"
            AppController.getmService().xmpp.updateChatStatus(rosterTable.id, ChatState.paused);
        }
    };

    private void startXmppService() {
        if (!XMPPService.isServiceRunning) {
            Intent intent = new Intent(this, XMPPService.class);
            mChatApp.UnbindService();
            mChatApp.BindService(intent);
        } else {
            xmppHandler = AppController.getmService().xmpp;
            if (!xmppHandler.isConnected()) {
                xmppHandler.connect();
            } else {
                xmppHandler.setUserPassword(CredentialManager.getUserName(), CredentialManager.getPassword());
                xmppHandler.login();
            }
        }
    }

    public void addlisttop(List<ChatItem> list) {
        Logger.d(list.size()+"    {{{{{{");
        if (list.size() < 15) {
            isLastPage = true;
        }
        for (int i = 0; i < list.size(); i++) {
            chatItemList.add(i, list.get(i));
            adapter.notifyItemInserted(i);
        }
        if (chatItemList.size() > 0) {
            messageId = chatItemList.get(0).getUid();
        }
        isLoading = false;
    }

    @OnClick(R.id.uploadImage)
    void uploadImage() {

        if (ActivityCompat.checkSelfPermission(ChatDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
        } else {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, REQUEST_CODE_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_GALLERY) {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, REQUEST_CODE_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri resultUri = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(resultUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(imageStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bitmap == null) {
                    Toast.makeText(getApplicationContext(), R.string.something_wrong, Toast.LENGTH_LONG).show();
                    return;
                }

                Logger.d(chatItemList.size());

//                showLoading();
//                imageUploadProgress.setVisibility(View.VISIBLE);
                ChatItem chat = new ChatItem("", rosterTable.name, mVCard.getField("URL"), rosterTable.nick_name, System.currentTimeMillis(), mVCard.getFrom().asBareJid().toString(), rosterTable.id, Constants.TYPE_IMAGE, true, "");
                chatItemList.add(chat);
                adapter.notifyItemInserted(chatItemList.size() - 1);
                rvChatDetails.scrollToPosition(chatItemList.size() - 1);
                Logger.d(chatItemList.size());
                AuthApiHelper.uploadImage(this, bitmap, new ImageUploadView() {
                    @Override
                    public void onImageUploadSuccess(String img, String smImg) {
//                        hideLoading();
//                        productImageList.add(img);
//                        imageUploadProgress.setVisibility(View.GONE);
//                        chatItemList.remove(chat);
//                        adapter.notifyItemRemoved(chatItemList.size()-1);
//                        Logger.d(chatItemList.size());

                        ChatItem chatItem = new ChatItem(smImg, rosterTable.name, mVCard.getField("URL"), rosterTable.nick_name, System.currentTimeMillis(), mVCard.getFrom().asBareJid().toString(), rosterTable.id, Constants.TYPE_IMAGE, true, img);

//                        chatItemList.add(chatItem);
//                        adapter.notifyItemInserted(chatItemList.size() - 1);
//                        rvChatDetails.smoothScrollToPosition(adapter.getItemCount() - 1);
                        try {
                            xmppHandler.sendMessage(chatItem);
                        } catch (SmackException e) {
                            e.printStackTrace();
                        }
//                        rvChatDetails.smoothScrollToPosition(chatItemList.size() - 1);

                    }

                    @Override
                    public void onImageUploadFailed(String msg) {
                        dialog.hideDialog();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                    }
                });


                Logger.d(bitmap.getHeight() + "    x    " + bitmap.getWidth());
            }
        }
    }

    @OnClick(R.id.ivSend)
    void send() {
        if (!etCommentsBox.getText().toString().trim().isEmpty() && mVCard != null) {

            ChatItem chatItem = new ChatItem(etCommentsBox.getText().toString().trim(), CredentialManager.getUserData().getFirst_name() + " " + CredentialManager.getUserData().getLast_name(), CredentialManager.getUserData().getImage_sm(), mVCard.getNickName(), System.currentTimeMillis(), mVCard.getFrom().asBareJid().toString(), rosterTable.id, Constants.TYPE_TEXT, true, "");
            chatItem.setUid(CredentialManager.getUserName() + System.currentTimeMillis());
//            chatItemList.add(chatItem);
            Logger.d(new Gson().toJson(chatItem));
//            adapter.notifyDataSetChanged();
//            rvChatDetails.smoothScrollToPosition(adapter.getItemCount() - 1);
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
    void back() {
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
