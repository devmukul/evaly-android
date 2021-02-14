package bd.com.evaly.evalyshop.ui.home.model;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyModel;


public abstract class EpoxyModelStaggeredGrid<T extends View> extends EpoxyModel<T> {

//    protected View buildView(@NonNull ViewGroup parent) {
//
//        Logger.e("called from buildView");
//
//        View view = LayoutInflater.from(parent.getContext()).inflate(getLayout(), parent, false);
//        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//        );
//
//        params.setFullSpan(true);
//        view.setLayoutParams(params);
//
//        return view;
//    }

    @Override
    public void bind(@NonNull T view) {

        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setFullSpan(true);
        view.setLayoutParams(params);
        super.bind(view);
    }

}