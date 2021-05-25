package bd.com.evaly.evalyshop.ui.evalyPoint;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentEvalyPointsBinding;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EvalyPointsFragment extends BaseFragment<FragmentEvalyPointsBinding, EvalyPointsViewModel> {


    public EvalyPointsFragment() {
        super(EvalyPointsViewModel.class, R.layout.fragment_evaly_points);
    }

    @Override
    protected void initViews() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

    }

    @Override
    protected void liveEventsObservers() {

    }

    @Override
    protected void clickListeners() {
        binding.toolbar.setNavigationOnClickListener(view1 -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });
    }

    @Override
    protected void setupRecycler() {


    }

}
