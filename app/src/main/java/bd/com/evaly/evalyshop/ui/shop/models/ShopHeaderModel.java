package bd.com.evaly.evalyshop.ui.shop.models;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ShopModelHeaderBinding;
import bd.com.evaly.evalyshop.models.catalog.shop.ShopDetailsResponse;
import bd.com.evaly.evalyshop.models.reviews.ReviewSummaryModel;
import bd.com.evaly.evalyshop.ui.epoxy.BaseDataBindingEpoxyModel;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.shop_model_header)
public abstract class ShopHeaderModel extends BaseDataBindingEpoxyModel {

    @EpoxyAttribute
    ShopDetailsResponse shopInfo;

    @EpoxyAttribute
    String description;

    @EpoxyAttribute
    boolean isSubscribed = false;

    @EpoxyAttribute
    int subCount = 0;

    @EpoxyAttribute
    ReviewSummaryModel ratingSummary;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener btn1OnClick;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener btn2OnClick;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener btn3OnClick;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener btn4OnClick;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener messageOnClick;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener followOnClick;

    @Override
    public void preBind(ViewDataBinding baseBinding) {
        super.preBind(baseBinding);

        ShopModelHeaderBinding binding = (ShopModelHeaderBinding) baseBinding;
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);

        binding.btn1Image.setOnClickListener(btn1OnClick);
        binding.btn1Title.setOnClickListener(btn1OnClick);

        binding.btn2Image.setOnClickListener(btn2OnClick);
        binding.btn2Title.setOnClickListener(btn2OnClick);

        binding.btn3Image.setOnClickListener(btn3OnClick);
        binding.btn3Title.setOnClickListener(btn3OnClick);

        binding.btn4Image.setOnClickListener(btn4OnClick);
        binding.btn4Title.setOnClickListener(btn4OnClick);

        binding.followBtn.setOnClickListener(followOnClick);
        binding.llInbox.setOnClickListener(messageOnClick);
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }
}
