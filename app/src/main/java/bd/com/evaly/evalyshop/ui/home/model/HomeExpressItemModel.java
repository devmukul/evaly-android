package bd.com.evaly.evalyshop.ui.home.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelExpressItemBinding;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.home_model_express_item)
public abstract class HomeExpressItemModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    ExpressServiceModel model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    public ExpressServiceModel getModel() {
        return model;
    }


    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        HomeModelExpressItemBinding binding = (HomeModelExpressItemBinding) holder.getDataBinding();

        Glide.with(binding.getRoot())
                .asBitmap()
                .load(model.getImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(new RequestOptions().override(1450, 460))
                .into(binding.image);

        binding.title.setText(model.getName());
        binding.getRoot().setOnClickListener(clickListener);

    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}