package bd.com.evaly.evalyshop.ui.shop.quickView.models;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.databinding.ItemShopCategorySmallBinding;
import bd.com.evaly.evalyshop.models.tabs.TabsItem;
import bd.com.evaly.evalyshop.util.Utils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_shop_category_small)
public abstract class SmallCategoryModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    String title;

    @EpoxyAttribute
    String image;

    @EpoxyAttribute
    boolean isSelected;

    @EpoxyAttribute
    public TabsItem model;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    public TabsItem getModel() {
        return model;
    }

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemShopCategorySmallBinding binding = (ItemShopCategorySmallBinding) holder.getDataBinding();

        binding.text.setText(Html.fromHtml(title));

        Glide.with(binding.getRoot())
                .load(image)
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
                })
                .skipMemoryCache(true)
                .apply(new RequestOptions().override(160, 160))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.ic_evaly_placeholder))
                .into(binding.image);

        binding.categoryItem.setOnClickListener(clickListener);

        binding.selectedBrd.bringToFront();

        if (isSelected)
            binding.selectedBrd.setVisibility(View.VISIBLE);
        else
            binding.selectedBrd.setVisibility(View.GONE);
    }

    @Override
    protected void setDataBindingVariables(ViewDataBinding binding) {
    }

    @Override
    public void unbind(@NonNull DataBindingHolder holder) {
        super.unbind(holder);
        holder.getDataBinding().unbind();
    }
}

