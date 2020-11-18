package bd.com.evaly.evalyshop.ui.shop.models;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ShopModelItemCategoryBinding;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.util.Utils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.shop_model_item_category)
public abstract class ShopCategoryItemModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    TabsItem model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    public TabsItem getModel() {
        return model;
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);
        ShopModelItemCategoryBinding binding = (ShopModelItemCategoryBinding) holder.getDataBinding();

        Glide.with(binding.getRoot())
                .load(model.getImage())
                .listener(new RequestListener<Drawable>() {
                              @Override
                              public boolean onLoadFailed(@androidx.annotation.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                  return false;
                              }

                              @SuppressLint("CheckResult")
                              @Override
                              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                  Observable.fromCallable(() -> Utils.changeColor(((BitmapDrawable) resource).getBitmap(),
                                          Color.parseColor("#ecf3f9"), Color.WHITE))
                                          .subscribeOn(Schedulers.io())
                                          .observeOn(AndroidSchedulers.mainThread())
                                          .subscribe(binding.image::setImageBitmap, throwable -> Log.e("error", "Throwable " + throwable.getMessage()));
                                  return true;
                              }
                          }
                )
                .placeholder(R.drawable.ic_placeholder_small)
                .apply(new RequestOptions().override(250, 250))
                .into(binding.image);

        binding.text.setText(model.getTitle());
        binding.categoryItem.setOnClickListener(clickListener);
    }


    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

}