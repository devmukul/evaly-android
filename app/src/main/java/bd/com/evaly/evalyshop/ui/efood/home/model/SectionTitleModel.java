package bd.com.evaly.evalyshop.ui.efood.home.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.EfoodModelSectionTitleBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.efood_model_section_title)
public abstract class SectionTitleModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String title;
    @EpoxyAttribute
    boolean showAll;
    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        EfoodModelSectionTitleBinding binding = (EfoodModelSectionTitleBinding) holder.getDataBinding();

        binding.title.setText(title);

        if (showAll) {
            binding.showAll.setVisibility(View.VISIBLE);
            binding.showAll.setOnClickListener(clickListener);
        } else
            binding.showAll.setVisibility(View.GONE);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}