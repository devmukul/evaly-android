package bd.com.evaly.evalyshop.ui.campaign;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentCampaignBinding;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;
import bd.com.evaly.evalyshop.ui.campaign.adapter.CampaignAdapter;

public class CampaignFragment extends Fragment implements CampaignNavigator {

    private FragmentCampaignBinding binding;
    private CampaignViewModel viewModel;
    private View mRootView;
    private List<CampaignItem> items;
    private CampaignAdapter adapter;
    private NavController navController;

    public static CampaignFragment newInstance() {
        final CampaignFragment fragment = new CampaignFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_campaign, container, false);
        binding.setViewModel(viewModel);
        mRootView = binding.getRoot();
        navController = NavHostFragment.findNavController(this);
        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //bottom sheet round corners can be obtained but the while background appears to remove that we need to add this.
        // setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);

        viewModel = new ViewModelProvider(this).get(CampaignViewModel.class);
        viewModel.setNavigator(this);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        viewModel.loadCampaigns();
        initRecycler();
    }


    private void initRecycler() {

        items = new ArrayList<>();

        adapter = new CampaignAdapter(getContext(), items, item -> {

            Bundle bundle = new Bundle();
            bundle.putSerializable("model", item);
            navController.navigate(R.id.campaignShopFragment, bundle);

        });
        binding.recyclerView.setAdapter(adapter);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onListLoaded(List<CampaignItem> list) {

        binding.progressBar.setVisibility(View.GONE);

        if (list.size() == 0) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.layoutNot.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.layoutNot.setVisibility(View.GONE);
        }

        items.addAll(list);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onListFailed(String errorBody, int errorCode) {

        Toast.makeText(mRootView.getContext(), "Error occurred", Toast.LENGTH_SHORT).show();
        binding.progressBar.setVisibility(View.INVISIBLE);

    }


}

