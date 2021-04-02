package bd.com.evaly.evalyshop.ui.refundSettlement;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.FragmentRefundSettlementBinding;
import bd.com.evaly.evalyshop.ui.base.BaseFragment;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RefundSettlementFragment extends BaseFragment<FragmentRefundSettlementBinding, RefundSettlementViewModel> {

    public RefundSettlementFragment() {
        super(RefundSettlementViewModel.class, R.layout.fragment_refund_settlement);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void liveEventsObservers() {

    }

    @Override
    protected void clickListeners() {


    }

}
