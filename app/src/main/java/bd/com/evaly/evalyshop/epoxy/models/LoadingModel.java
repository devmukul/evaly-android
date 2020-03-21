package bd.com.evaly.evalyshop.epoxy.models;

import android.view.View;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;
import butterknife.ButterKnife;

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
        protected void bindView(View itemView) {

            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
            params.setFullSpan(true);

            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
