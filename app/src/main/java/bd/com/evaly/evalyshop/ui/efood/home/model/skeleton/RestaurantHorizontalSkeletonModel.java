package bd.com.evaly.evalyshop.ui.efood.home.model.skeleton;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.EfoodItemRestaurantHorizontalBinding;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.efood_item_restaurant_horizontal_skeleton_skeleton)
public abstract class RestaurantHorizontalSkeletonModel extends DataBindingEpoxyModel {


    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}
