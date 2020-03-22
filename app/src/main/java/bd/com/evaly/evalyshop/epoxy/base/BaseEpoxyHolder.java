package bd.com.evaly.evalyshop.epoxy.base;


import android.view.View;

import androidx.annotation.CallSuper;

import com.airbnb.epoxy.EpoxyHolder;

import butterknife.ButterKnife;

public abstract class BaseEpoxyHolder extends EpoxyHolder {

    public View itemView;

    @CallSuper
    @Override
    protected void bindView(View itemView) {
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
    }

    public View getItemView() {
        return itemView;
    }
}