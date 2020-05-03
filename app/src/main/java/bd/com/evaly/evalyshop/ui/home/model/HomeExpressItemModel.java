package bd.com.evaly.evalyshop.ui.home.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.controller.AppController;
import bd.com.evaly.evalyshop.databinding.HomeModelExpressItemBinding;
import bd.com.evaly.evalyshop.models.express.ExpressServiceModel;
import bd.com.evaly.evalyshop.util.Utils;

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

        int drawableBg = R.drawable.btn_express_default;

        String name = model.getName();

        if (name.contains("Meat"))
            name  = name.replace("Market", "");

        int wordCount = Utils.countWords(name);

        if (wordCount > 1 && wordCount < 4) {
            String firstWord = name.substring(0, model.getName().indexOf(' '));
            name = name.replace(firstWord+" ", firstWord + "\n");
        }

//        if (model.getSlug().contains("bullet")) {
//            drawableBg = R.drawable.btn_express_bullet;
//           // binding.image.setImageDrawable(AppController.getmContext().getDrawable(R.drawable.ic_bullet_express));
//            Glide.with(binding.getRoot())
//                    .load(R.drawable.ic_bullet_express)
//                    .into(binding.image);
//        } else if (model.getSlug().contains("grocery")) {
//            drawableBg = R.drawable.btn_express_grocery;
//            binding.image.setImageDrawable(AppController.getmContext().getDrawable(R.drawable.ic_color_ingredients));
//            binding.image.setPadding(20, 20, 20, 20);
//        } else if (model.getSlug().contains("pharmacy")) {
//            drawableBg = R.drawable.btn_express_foods;
//            binding.image.setImageDrawable(AppController.getmContext().getDrawable(R.drawable.ic_pill));
//            binding.image.setPadding(40, 40, 40, 40);
//
//        } else if (model.getSlug().contains("meat")) {
//            drawableBg = R.drawable.btn_express_pharmacy;
//            binding.image.setImageDrawable(AppController.getmContext().getDrawable(R.drawable.ic_fish_meat));
//            binding.image.setPadding(20, 20, 20, 20);
//        } else {
//
//        }

        Glide.with(binding.getRoot())
                .asBitmap()
                .load(model.getImage())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(binding.image);
        binding.image.setPadding(0, 0, 0, 0);

        binding.holder.setBackground(AppController.getmContext().getDrawable(drawableBg));


        binding.title.setText(name);
        binding.getRoot().setOnClickListener(clickListener);

    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}