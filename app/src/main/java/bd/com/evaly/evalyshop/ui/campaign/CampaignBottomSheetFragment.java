package bd.com.evaly.evalyshop.ui.campaign;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.activity.CampaignShopActivity;
import bd.com.evaly.evalyshop.databinding.BottomSheetCampaignBinding;
import bd.com.evaly.evalyshop.models.campaign.CampaignItem;

public class CampaignBottomSheetFragment extends BottomSheetDialogFragment implements CampaignBottomSheetNavigator{

    private BottomSheetCampaignBinding binding;
    private CampaignBottomSheetViewModel viewModel;
    private View mRootView;
    private List<CampaignItem> items = new ArrayList<>();
    private CampaignAdapter adapter;

    public static CampaignBottomSheetFragment newInstance() {
        final CampaignBottomSheetFragment fragment = new CampaignBottomSheetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_campaign, container, false);
        binding.setViewModel(viewModel);
        mRootView = binding.getRoot();
        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //bottom sheet round corners can be obtained but the while background appears to remove that we need to add this.
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);

        viewModel = ViewModelProviders.of(this).get(CampaignBottomSheetViewModel.class);
        viewModel.setNavigator(this);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        viewModel.loadCampaigns();
        initRecycler();
    }


   private void initRecycler(){

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        binding.recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CampaignAdapter(getContext(), items, new CampaignListener() {
            @Override
            public void onItemClick(CampaignItem item) {
                Intent ni = new Intent(getContext(), CampaignShopActivity.class);
                ni.putExtra("title", item.getName());
                ni.putExtra("slug", item.getSlug());
                getContext().startActivity(ni);

                CampaignBottomSheetFragment.this.dismiss();

            }
        });
        binding.recyclerView.setAdapter(adapter);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog dialogz = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet =  dialogz.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
            BottomSheetBehavior.from(bottomSheet).setHideable(true);
        });
        return bottomSheetDialog;
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
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }


    @Override
    public void onListLoaded(List<CampaignItem> list) {

        binding.progressBar.setVisibility(View.GONE);

        if (list.size()==0) {
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

        Toast.makeText(getContext(), "Error occured", Toast.LENGTH_SHORT).show();
        binding.progressBar.setVisibility(View.INVISIBLE);

    }
}
