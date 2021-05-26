package bd.com.evaly.evalyshop.ui.evalyPoint;

import java.util.ArrayList;
import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentEvalyPointsBinding;
import bd.com.evaly.evalyshop.models.points.FaqItem;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import bd.com.evaly.evalyshop.ui.evalyPoint.controller.PointFaqController;
import bd.com.evaly.evalyshop.util.BindingUtils;
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
        viewModel.pointsLiveData.observe(getViewLifecycleOwner(), integer -> {
            BindingUtils.bindPointsView(binding.pointGraph, integer, false);
        });
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

        // FAQ recycler

        PointFaqController controller = new PointFaqController();

        List<FaqItem> list = new ArrayList<>();
        list.add(new FaqItem(
                "What is the Evaly Points?",
                "Earn score by purchasing in evaly regular shop and cyclone campaign.",
                true));
        list.add(new FaqItem(
                "What are the benefits of Evaly Points?",
                "As an evaly user, you will get faster delivery based on your score. Higher score means a faster delivery schedule.",
                false));
        list.add(new FaqItem(
                "How can I earn an Evaly Points?",
                "You can earn points by making payments in your orders. Both Cyclone and regular shops. Please note that, in regular time the score is higher than the cyclone orders.",
                false));
        list.add(new FaqItem(
                "What is Loyalty level?",
                "Based on your score there will be 4 royalty levels such as Bronze, Silver, Gold, Diamond and Platinum.",
                false));

        controller.setList(list);
        binding.recyclerFaq.setAdapter(controller.getAdapter());
        controller.requestModelBuild();

    }

}
