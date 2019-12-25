package bd.com.evaly.evalyshop.ui.transaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bd.com.evaly.evalyshop.AppController;
import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.transaction.adapter.TransactionHistoryAdapter;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.TransactionItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.util.UserDetails;

public class TransactionHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    TransactionHistoryAdapter adapter;
    ArrayList<TransactionItem> itemList;
    UserDetails userDetails;
    LinearLayout not;
    String userAgent;
    ProgressBar progressBar;
    int currentPage = 0;

    NestedScrollView nestedSV;
    TextView balance;


    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Transaction History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        balance = findViewById(R.id.balance);

        nestedSV = findViewById(R.id.stickyScrollView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.recycle);
        not=findViewById(R.id.empty);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        itemList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(itemList,this);
        recyclerView.setAdapter(adapter);
        userDetails = new UserDetails(this);

        balance.setText("৳ " +userDetails.getBalance());

        queue = Volley.newRequestQueue(this);

        getBalance();
        getTransactionHistory(++currentPage);

        if (nestedSV != null) {
            nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()))
                        getTransactionHistory(++currentPage);
            });
        }

    }



    public void getTransactionHistory(int page){

        progressBar.setVisibility(View.VISIBLE);

        AuthApiHelper.getTransactionHistory(CredentialManager.getToken(), CredentialManager.getUserName(), page, new ResponseListenerAuth<CommonDataResponse<List<TransactionItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<TransactionItem>> response, int statusCode) {

                progressBar.setVisibility(View.INVISIBLE);

                if (response != null){
                    itemList.addAll(response.getData());
                    adapter.notifyDataSetChanged();

                    if (response.getData().size() == 0 && page == 1){
                        not.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAuthError(boolean logout) {

                if (logout)
                    AppController.logout(TransactionHistory.this);
                else
                    getTransactionHistory(page);
            }
        });

    }



    public void getBalance() {

        AuthApiHelper.getUserInfoPay(CredentialManager.getToken(), CredentialManager.getUserName(), new ResponseListenerAuth<JsonObject, String>() {
            @Override
            public void onDataFetched(JsonObject response, int statusCode) {
                response = response.getAsJsonObject("data");
                userDetails.setBalance(response.get("balance").getAsString());
                balance.setText(String.format(Locale.ENGLISH, "৳ %s", response.get("balance").getAsString()));
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {
                if (logout)
                    AppController.logout(TransactionHistory.this);
                else
                    getBalance();

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
