package bd.com.evaly.evalyshop.ui.evalyPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        viewModel.pointsLiveData.observe(getViewLifecycleOwner(), model -> {
            BindingUtils.bindPointsView(binding.pointGraph, model, false);
            if (model != null && model.getInterval().size() > 4) {
                binding.btn1Points.setText(formatPoints(model.getInterval().get(0)));
                binding.btn2Points.setText(formatPoints(model.getInterval().get(1)));
                binding.btn3Points.setText(formatPoints(model.getInterval().get(2)));
                binding.btn4Points.setText(formatPoints(model.getInterval().get(3)));
                binding.btn5Points.setText(formatPoints(model.getInterval().get(4)));
            }
        });
    }

    private String formatPoints(int point){
        return String.format(Locale.ENGLISH, "%,d Pts", (int) point);
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
                "What is the ePoint?",
                "ePoints are earned by making a new purchase in any payment gateway of Evaly (except gift cards).",
                true));
        list.add(new FaqItem(
                "What are the benefits of ePoints?",
                "As an Evaly user, you will get faster delivery based on your ePoints. In short - the more points you have, the sooner you will get the delivery.",
                false));
        list.add(new FaqItem(
                "How can I earn an ePoints?",
                "You can earn ePoints by making payments in your orders. Please note that, regular shop orders will earn you more points than the Cyclone shop orders.",
                false));
        list.add(new FaqItem(
                "What is Loyalty Level?",
                "Based on your score there will be 5 loyalty levels such as eBronze, eSilver, eGold, eDiamond and ePlatinum. Higher level will have more benefits.",
                false));

        controller.setList(list);
        binding.recyclerFaq.setAdapter(controller.getAdapter());
        controller.requestModelBuild();

    }

}
