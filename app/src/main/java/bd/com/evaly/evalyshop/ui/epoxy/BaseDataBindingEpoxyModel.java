package bd.com.evaly.evalyshop.ui.epoxy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import java.util.List;

public abstract class BaseDataBindingEpoxyModel extends EpoxyModelWithHolder<BaseDataBindingEpoxyModel.DataBindingHolder> {

    @Override
    public View buildView(@NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, getViewType(), parent, false);
        View view = binding.getRoot();
        view.setTag(binding);
        preBind(binding);
        return view;
    }

    public void preBind(ViewDataBinding baseBinding) {

    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        setDataBindingVariables(holder.dataBinding);
        holder.dataBinding.executePendingBindings();
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder, @NonNull EpoxyModel<?> previouslyBoundModel) {
        setDataBindingVariables(holder.dataBinding, previouslyBoundModel);
        holder.dataBinding.executePendingBindings();
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder, @NonNull List<Object> payloads) {
        setDataBindingVariables(holder.dataBinding, payloads);
        holder.dataBinding.executePendingBindings();
    }

    /**
     * This is called when the model is bound to a view, and the view's variables should be updated
     * with the model's data. {@link ViewDataBinding#executePendingBindings()} is called for you after
     * this method is run.
     * <p>
     * If you leave your class abstract and have a model generated for you via annotations this will
     * be implemented for you. However, you may choose to implement this manually if you like.
     */
    protected abstract void setDataBindingVariables(ViewDataBinding binding);

    /**
     * Similar to {@link #setDataBindingVariables(ViewDataBinding)}, but this method only binds
     * variables that have changed. The changed model comes from {@link #bind(DataBindingHolder,
     * EpoxyModel)}. This will only be called if the model is used in an {@link EpoxyController}
     * <p>
     * If you leave your class abstract and have a model generated for you via annotations this will
     * be implemented for you. However, you may choose to implement this manually if you like.
     */
    protected void setDataBindingVariables(ViewDataBinding dataBinding,
                                           EpoxyModel<?> previouslyBoundModel) {
        setDataBindingVariables(dataBinding);
    }

    protected void setDataBindingVariables(ViewDataBinding dataBinding, List<Object> payloads) {
        setDataBindingVariables(dataBinding);
    }

    @Override
    public void unbind(@NonNull DataBindingHolder holder) {
        holder.dataBinding.unbind();
    }

    @Override
    protected final DataBindingHolder createNewHolder(@NonNull ViewParent parent) {
        return new DataBindingHolder();
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