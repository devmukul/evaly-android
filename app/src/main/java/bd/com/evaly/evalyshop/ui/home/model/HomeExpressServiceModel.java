package bd.com.evaly.evalyshop.ui.home.model;

import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelExpressServiceBinding;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.home_model_express_service)
public abstract class HomeExpressServiceModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    ExpressServiceModel model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @EpoxyAttribute
    int fontSize;

    public ExpressServiceModel getModel() {
        return model;
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        HomeModelExpressServiceBinding binding = (HomeModelExpressServiceBinding) holder.getDataBinding();

        if (binding.getRoot().getContext() != null)
            Glide.with(binding.getRoot())
                    .asBitmap()
                    .load(model.getAppLogo() == null ? model.getImage() : model.getAppLogo())
                    .placeholder(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.bg_f8f8f8_round))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(binding.image);

        binding.image.setPadding(0, 0, 0, 0);

        binding.title.setText(model.getAppName().replace("\\n", "\n"));
        if (fontSize > 0) {
            binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        }
        binding.getRoot().setOnClickListener(clickListener);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {

    }

}