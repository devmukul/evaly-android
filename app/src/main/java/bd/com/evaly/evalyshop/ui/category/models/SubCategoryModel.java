package bd.com.evaly.evalyshop.ui.category.models;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
import bd.com.evaly.evalyshop.data.roomdb.categories.CategoryEntity;
import bd.com.evaly.evalyshop.databinding.ItemSubCategoryBinding;
import bd.com.evaly.evalyshop.util.Utils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.item_sub_category)
public abstract class SubCategoryModel extends DataBindingEpoxyModel {

    @EpoxyAttribute
    public AppCompatActivity activity;
    @EpoxyAttribute
    public CategoryEntity model;
    @EpoxyAttribute
    public boolean isShop;

    @EpoxyAttribute(DoNotHash)
    View.OnClickListener clickListener;

    @Override
    public void bind(@NonNull DataBindingHolder holder) {
        super.bind(holder);

        ItemSubCategoryBinding binding = (ItemSubCategoryBinding) holder.getDataBinding();
        if (binding.getRoot().getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(false);
        }

        binding.title.setText(Html.fromHtml(model.getName()));

        Glide.with(binding.getRoot())
                .load(model.getImageUrl())
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
                .apply(new RequestOptions().override(260, 260))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(ContextCompat.getDrawable(binding.getRoot().getContext(), R.drawable.ic_evaly_placeholder))
                .into(binding.image);

        binding.getRoot().setOnClickListener(clickListener);
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

