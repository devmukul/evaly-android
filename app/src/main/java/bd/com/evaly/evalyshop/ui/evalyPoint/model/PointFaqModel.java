package bd.com.evaly.evalyshop.ui.evalyPoint.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemPointFaqBinding;
import bd.com.evaly.evalyshop.models.points.FaqItem;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_point_faq)
public abstract class PointFaqModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    FaqItem model;

    @EpoxyAttribute
    boolean isExpanded;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ItemPointFaqBinding binding = (ItemPointFaqBinding) holder.getDataBinding();
        binding.title.setText(model.getTitle());
        binding.description.setText(model.getDescription());
        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}

