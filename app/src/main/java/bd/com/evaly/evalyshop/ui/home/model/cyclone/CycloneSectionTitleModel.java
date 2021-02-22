package bd.com.evaly.evalyshop.ui.home.model.cyclone;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemCycloneTitleBinding;
import bd.com.evaly.evalyshop.ui.epoxy.BaseDataBindingEpoxyModel;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_cyclone_title)
public abstract class CycloneSectionTitleModel extends BaseDataBindingEpoxyModel {

    @EpoxyAttribute
    String title;

    @EpoxyAttribute
    boolean showMore;
    
    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void preBind(ViewDataBinding baseBinding) {
        super.preBind(baseBinding);
        ItemCycloneTitleBinding binding = (ItemCycloneTitleBinding) baseBinding;
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
        params.setFullSpan(true);
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemCycloneTitleBinding binding = (ItemCycloneTitleBinding) holder.getDataBinding();

        if (title != null)
            binding.title.setText(title);

        if (showMore)
            binding.showMore.setVisibility(View.VISIBLE);
        else
            binding.showMore.setVisibility(View.GONE);

        binding.showMore.setOnClickListener(clickListener);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

}
