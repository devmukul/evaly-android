package bd.com.evaly.evalyshop.ui.chat;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.BaseActivity;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.listener.DataFetchingListener;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.models.chat.RequestedUserModel;
import bd.com.evaly.evalyshop.models.db.RosterTable;
import bd.com.evaly.evalyshop.models.xmpp.ChatItem;
import bd.com.evaly.evalyshop.util.Constants;
import bd.com.evaly.evalyshop.util.ViewDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class RequestListActivity extends BaseActivity implements RequestListAdapter.OnAcceptRejectListener {

    @BindView(R.id.rvRequest)
    RecyclerView rvRequest;
    @BindView(R.id.noItem)
    LinearLayout noitem;

    private RequestListAdapter adapter;
    private List<RequestedUserModel> requestList;

    private ViewDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);

        ButterKnife.bind(this);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Message Request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ViewDialog(this);

        requestList = new ArrayList<>();

        adapter = new RequestListAdapter(requestList, this, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvRequest.setLayoutManager(layoutManager);
        rvRequest.setAdapter(adapter);

        dialog.showDialog();
        AuthApiHelper.getInvitationList(CredentialManager.getUserName(), new DataFetchingListener<Response<JsonArray>>() {
            @Override
            public void onDataFetched(Response<JsonArray> response) {
                dialog.hideDialog();
                if (response.code() == 200 || response.code() == 201) {
                    List<RequestedUserModel> list = new Gson().fromJson(response.body(), new TypeToken<List<RequestedUserModel>>() {
                    }.getType());
                    requestList.addAll(list);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(int status) {
                dialog.hideDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestAccept(RequestedUserModel model, String name) {
        addRoster(model, name, "both");
    }

    @Override
    public void onRequestReject(RequestedUserModel model, String name) {
        addRoster(model, name, "none");

    }

    private void addRoster(RequestedUserModel model, String name, String type) {
        String[] user = model.getUser_jid().split("@");

        HashMap<String, String> data = new HashMap<>();
        data.put("localuser", CredentialManager.getUserName());
        data.put("localserver", Constants.XMPP_HOST);
        data.put("user", user[0]);
        data.put("server", Constants.XMPP_HOST);
        data.put("nick", name);
        data.put("subs", type);
        data.put("group", "evaly");

        AuthApiHelper.addRoster(data, new DataFetchingListener<Response<JsonPrimitive>>() {
            @Override
            public void onDataFetched(Response<JsonPrimitive> response) {


                if (response.code() == 200 || response.code() == 201) {
                    RosterTable table = new RosterTable();
                    table.id = model.getUser_jid();
                    table.rosterName = name;
                    table.name = name;
                    table.status = 0;
                    table.unreadCount = 1;

                    requestList.remove(model);
                    adapter.notifyDataSetChanged();


                    if (type.equalsIgnoreCase("both")){
                        if (AppController.getmService() != null && AppController.getmService().xmpp != null && AppController.getmService().xmpp.isLoggedin()){
                            ChatItem chatItem = new ChatItem("Your request has been accepted", CredentialManager.getUserData().getFirst_name() + " " + CredentialManager.getUserData().getLast_name(), CredentialManager.getUserData().getImage_sm(), CredentialManager.getUserData().getFirst_name(), System.currentTimeMillis(), CredentialManager.getUserName() + "@" + Constants.XMPP_HOST, model.getUser_jid(), Constants.TYPE_TEXT, true, "");
                            chatItem.setInvitation(false);
                            chatItem.setAccepted(true);
                            try {
                                AppController.getmService().xmpp.sendMessage(chatItem);
                            } catch (SmackException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(getApplicationContext(), "Request accepted!", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Request rejected!", Toast.LENGTH_LONG).show();
                    }


                } else if (response.code() == 401) {
                    AuthApiHelper.refreshToken(RequestListActivity.this, new DataFetchingListener<Response<JsonObject>>() {
                        @Override
                        public void onDataFetched(Response<JsonObject> response) {
                            addRoster(model, name, type);
                        }

                        @Override
                        public void onFailed(int status) {

                        }
                    });
                }
            }

            @Override
            public void onFailed(int status) {

            }
        });
    }

}
