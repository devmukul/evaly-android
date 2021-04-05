package bd.com.evaly.evalyshop.ui.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.FragmentTransactionHistoryBinding;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.manager.CredentialManager;
import bd.com.evaly.evalyshop.models.CommonDataResponse;
import bd.com.evaly.evalyshop.models.transaction.TransactionItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.balance.BalanceViewModel;
import bd.com.evaly.evalyshop.ui.transaction.adapter.TransactionHistoryAdapter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TransactionHistory extends Fragment {

    @Inject
    ApiRepository apiRepository;
    private TransactionHistoryAdapter adapter;
    private ArrayList<TransactionItem> itemList;
    private int currentPage = 0;
    private FragmentTransactionHistoryBinding binding;
    private BalanceViewModel balanceViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionHistoryBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        balanceViewModel = new ViewModelProvider(this).get(BalanceViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);
        itemList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(itemList, getContext());
        binding.recyclerView.setAdapter(adapter);

        getTransactionHistory(++currentPage);

        binding.stickyScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()))
                getTransactionHistory(++currentPage);
        });

        balanceViewModel.getData().observe(getViewLifecycleOwner(), balanceModel -> {
            binding.balanceHolder.setVisibility(View.VISIBLE);
            binding.balanceHolder2.setVisibility(View.VISIBLE);
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
        apiRepository.getTransactionHistory(CredentialManager.getToken(), CredentialManager.getUserName(), page,
                new ResponseListenerAuth<CommonDataResponse<List<TransactionItem>>, String>() {
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
                            AppController.getInstance().logout(getActivity());
                        else
                            getTransactionHistory(page);
                    }
                });
    }

}
