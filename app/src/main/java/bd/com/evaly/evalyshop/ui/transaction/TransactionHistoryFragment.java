package bd.com.evaly.evalyshop.ui.transaction;

import android.view.View;

import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.data.preference.PreferenceRepository;
import bd.com.evaly.evalyshop.databinding.FragmentTransactionHistoryBinding;
import bd.com.evaly.evalyshop.models.transaction.TransactionItem;
import bd.com.evaly.evalyshop.rest.ApiRepository;
import bd.com.evaly.evalyshop.ui.balance.BalanceViewModel;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.transaction.adapter.TransactionHistoryAdapter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TransactionHistoryFragment extends BaseFragment<FragmentTransactionHistoryBinding, TransactionHistoryViewModel> {

    @Inject
    ApiRepository apiRepository;
    @Inject
    PreferenceRepository preferenceRepository;
    @Inject
    FirebaseRemoteConfig firebaseRemoteConfig;
    private TransactionHistoryAdapter adapter;
    private List<TransactionItem> itemList;
    private BalanceViewModel balanceViewModel;

    public TransactionHistoryFragment(){
        super(TransactionHistoryViewModel.class, R.layout.fragment_transaction_history);
    }

    @Override
    protected void initViews() {
        balanceViewModel = new ViewModelProvider(this).get(BalanceViewModel.class);
        binding.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        balanceViewModel.updateBalance();
    }

    @Override
    protected void liveEventsObservers() {

        viewModel.loadingBar.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                binding.progressBar.setVisibility(View.VISIBLE);
            else
                binding.progressBar.setVisibility(View.GONE);
        });

        viewModel.liveList.observe(getViewLifecycleOwner(), response -> {
            binding.progressBar.setVisibility(View.INVISIBLE);
            itemList.clear();
            itemList.addAll(response);
            adapter.notifyDataSetChanged();

            if (response.size() == 0 && viewModel.getPage() == 1) {
                binding.empty.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            }
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
            preferenceRepository.setBalance(balanceModel.getBalance());
        });

    }

    @Override
    protected void clickListeners() {

    }

    @Override
    protected void setupRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(manager);
        itemList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(itemList, getContext());
        binding.recyclerView.setAdapter(adapter);
        binding.stickyScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()))
                viewModel.getTransactionHistory();
        });
    }
}
