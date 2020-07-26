package bd.com.evaly.evalyshop.ui.home.model;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.HomeModelExpressItemNewBinding;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.util.Utils;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.home_model_express_item_new)
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
        HomeModelExpressItemNewBinding binding = (HomeModelExpressItemNewBinding) holder.getDataBinding();

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

        if (model.getAppBgColor() == null || model.getAppBgColor().equals("")) {
        } else
            try {
                binding.overly.setBackgroundColor(Color.parseColor(model.getAppBgColor()));
            } catch (Exception ignored) {
            }

        Glide.with(binding.getRoot())
                .asBitmap()
                .load(model.getAppLogo() == null ? model.getImage() : model.getAppLogo())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.image);

        binding.image.setPadding(0, 0, 0, 0);

        binding.title.setText(name.replace("\\n", "\n"));
        binding.overly.setOnClickListener(clickListener);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}