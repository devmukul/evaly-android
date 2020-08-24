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

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.transaction.TransactionItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.transaction.adapter.TransactionHistoryAdapter;
import bd.com.evaly.evalyshop.util.Utils;

public class TransactionHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    TransactionHistoryAdapter adapter;
    ArrayList<TransactionItem> itemList;
    LinearLayout not;
    ProgressBar progressBar;
    int currentPage = 0;

    NestedScrollView nestedSV;
    TextView balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(getString(R.string.transaction_history));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        balance = findViewById(R.id.balance);

        nestedSV = findViewById(R.id.stickyScrollView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recycle);
        not = findViewById(R.id.empty);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        itemList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(itemList, this);
        recyclerView.setAdapter(adapter);
        balance.setText(Utils.formatPriceSymbol(CredentialManager.getBalance()));


        getBalance();
        getTransactionHistory(++currentPage);

        if (nestedSV != null) {
            nestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()))
                    getTransactionHistory(++currentPage);
            });
        }

    }


    public void getTransactionHistory(int page) {

        progressBar.setVisibility(View.VISIBLE);


        AuthApiHelper.getTransactionHistory(CredentialManager.getToken(), CredentialManager.getUserName(), page, new ResponseListenerAuth<CommonDataResponse<List<TransactionItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<TransactionItem>> response, int statusCode) {

                progressBar.setVisibility(View.INVISIBLE);


                itemList.addAll(response.getData());
                adapter.notifyDataSetChanged();

                if (response.getData().size() == 0 && page == 1) {
                    not.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
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
                CredentialManager.setBalance(response.get("balance").getAsDouble());
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
