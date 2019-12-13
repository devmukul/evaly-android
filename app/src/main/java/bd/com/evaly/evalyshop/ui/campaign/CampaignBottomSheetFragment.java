package bd.com.evaly.evalyshop.ui.campaign;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.BottomSheetCampaignBinding;

public class CampaignBottomSheetFragment extends BottomSheetDialogFragment implements CampaignBottomSheetNavigator{

    BottomSheetCampaignBinding binding;
    CampaignBottomSheetViewModel viewModel;
    View mRootView;

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



}
