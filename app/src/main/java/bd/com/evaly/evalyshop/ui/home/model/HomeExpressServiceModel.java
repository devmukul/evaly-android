package bd.com.evaly.evalyshop.ui.home.model;

import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelExpressServiceBinding;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.util.Utils;

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

        String name = model.getName();

        if (model.getAppName() == null) {
            if (name.contains("Meat"))
                name = name.replace("Market", "");
            int wordCount = Utils.countWords(name);
            if (wordCount > 1 && wordCount < 4) {
                String firstWord = name.substring(0, model.getName().indexOf(' '));
                name = name.replace(firstWord + " ", firstWord + "\n");
            }
        } else
            name = model.getAppName();

        Glide.with(binding.getRoot())
                .asBitmap()
                .load(model.getAppLogo() == null ? model.getImage() : model.getAppLogo())
                .placeholder(R.drawable.bg_fafafa_round)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.image);

        binding.image.setPadding(0, 0, 0, 0);

        binding.title.setText(name.replace("\\n", "\n"));
        if (fontSize > 0)
            binding.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        binding.getRoot().setOnClickListener(clickListener);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}