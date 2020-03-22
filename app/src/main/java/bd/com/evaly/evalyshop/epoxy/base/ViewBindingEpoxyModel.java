package bd.com.evaly.evalyshop.epoxy.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import java.util.List;

public abstract class ViewBindingEpoxyModel extends EpoxyModelWithHolder<ViewBindingEpoxyModel.DataBindingHolder> {

    @Override
    protected View buildView(@NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, getViewType(), parent, false);
        View view = binding.getRoot();
        view.setTag(binding);
        return view;
    }

    @Override
    public void bind(@NonNull ViewBindingEpoxyModel.DataBindingHolder holder) {
        setDataBindingVariables(holder.dataBinding);
        holder.dataBinding.executePendingBindings();
    }

    @Override
    public void bind(@NonNull ViewBindingEpoxyModel.DataBindingHolder holder, @NonNull EpoxyModel<?> previouslyBoundModel) {
        setDataBindingVariables(holder.dataBinding, previouslyBoundModel);
        holder.dataBinding.executePendingBindings();
    }

    @Override
    public void bind(@NonNull ViewBindingEpoxyModel.DataBindingHolder holder, @NonNull List<Object> payloads) {
        setDataBindingVariables(holder.dataBinding, payloads);
        holder.dataBinding.executePendingBindings();
    }


    protected abstract void setDataBindingVariables(ViewDataBinding binding);


    protected void setDataBindingVariables(ViewDataBinding dataBinding,
                                           EpoxyModel<?> previouslyBoundModel) {
        setDataBindingVariables(dataBinding);
    }

    protected void setDataBindingVariables(ViewDataBinding dataBinding, List<Object> payloads) {
        setDataBindingVariables(dataBinding);
    }

    @Override
    public void unbind(@NonNull ViewBindingEpoxyModel.DataBindingHolder holder) {
        holder.dataBinding.unbind();
    }

    @Override
    protected final ViewBindingEpoxyModel.DataBindingHolder createNewHolder() {
        return new ViewBindingEpoxyModel.DataBindingHolder();
    }

    public static class DataBindingHolder extends EpoxyHolder {
        private ViewDataBinding dataBinding;

        public ViewDataBinding getDataBinding() {
            return dataBinding;
        }

        @Override
        protected void bindView(@NonNull View itemView) {
            dataBinding = (ViewDataBinding) itemView.getTag();
        }
    }
}

