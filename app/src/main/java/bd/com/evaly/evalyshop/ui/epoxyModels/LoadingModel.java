package bd.com.evaly.evalyshop.ui.epoxyModels;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;

@EpoxyModelClass(layout = R.layout.progressbar_layout)
public abstract class LoadingModel extends EpoxyModelWithHolder<LoadingModel.Holder> {
    @EpoxyAttribute
    String title;

    @Override
    public void bind(Holder holder) {

    }

    static class Holder extends EpoxyHolder {

        View itemView;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }

}
