package bd.com.evaly.evalyshop.ui.epoxy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyModel;
import com.orhanobut.logger.Logger;

public abstract class BaseEpoxyModel<T> extends EpoxyModel<T> {

    @Override
    protected View buildView(@NonNull ViewGroup parent) {


        View view = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
        preBind(view);
        return view;
    }

    public void preBind(View view) {

        Logger.e("base buildView called");
    }


    @Override
    public void bind(@NonNull T view) {
        super.bind(view);


    }

    @Override
    protected int getDefaultLayout() {
        return 0;
    }

}
