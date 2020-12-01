package bd.com.evaly.evalyshop.ui.transaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.ActivityTransactionHistoryBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.transaction.TransactionItem;
import bd.com.evaly.evalyshop.rest.apiHelper.AuthApiHelper;
import bd.com.evaly.evalyshop.ui.balance.BalanceViewModel;
import bd.com.evaly.evalyshop.ui.transaction.adapter.TransactionHistoryAdapter;

public class TransactionHistory extends AppCompatActivity {

    private TransactionHistoryAdapter adapter;
    private ArrayList<TransactionItem> itemList;
    private int currentPage = 0;
    private ActivityTransactionHistoryBinding binding;
    private BalanceViewModel balanceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        balanceViewModel = new ViewModelProvider(this).get(BalanceViewModel.class);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(getString(R.string.transaction_history));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(manager);
        itemList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(itemList, this);
        binding.recyclerView.setAdapter(adapter);

        getTransactionHistory(++currentPage);

        binding.stickyScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()))
                getTransactionHistory(++currentPage);
        });

        balanceViewModel.getData().observe(this, balanceModel -> {
            binding.balanceHolder.setVisibility(View.VISIBLE);
            binding.balanceHolder2.setVisibility(View.VISIBLE);
            binding.progressBarTop.setVisibility(View.GONE);
            binding.tvBalance.setText(String.format("৳ %s", balanceModel.getBalance()));
            binding.tvHoldingBalance.setText(String.format("৳ %s", balanceModel.getHoldingBalance()));
            binding.tvGiftCardBalance.setText(String.format("৳ %s", balanceModel.getGiftCardBalance()));
            binding.tvCashbackBalance.setText(String.format("৳ %s", balanceModel.getCashbackBalance()));
            if (balanceModel.getCashbackBalance() == 0)
                binding.claimCashback.setVisibility(View.GONE);
            else
                binding.claimCashback.setVisibility(View.VISIBLE);
            CredentialManager.setBalance(balanceModel.getBalance());
        });

        balanceViewModel.updateBalance();

    }


    public void getTransactionHistory(int page) {
        binding.progressBar.setVisibility(View.VISIBLE);
        AuthApiHelper.getTransactionHistory(CredentialManager.getToken(), CredentialManager.getUserName(), page, new ResponseListenerAuth<CommonDataResponse<List<TransactionItem>>, String>() {
            @Override
            public void onDataFetched(CommonDataResponse<List<TransactionItem>> response, int statusCode) {
                binding.progressBar.setVisibility(View.INVISIBLE);
                itemList.addAll(response.getData());
                adapter.notifyDataSetChanged();

                if (response.getData().size() == 0 && page == 1) {
                    binding.empty.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {
                binding.progressBar.setVisibility(View.GONE);
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
