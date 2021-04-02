package bd.com.evaly.evalyshop.ui.epoxy;

import android.view.View;
import android.widget.LinearLayout;

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

    public static class Holder extends EpoxyHolder {
        public View itemView;
        public LinearLayout container;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;
            if (itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
                params.setFullSpan(true);
            }
            container = itemView.findViewById(R.id.container);
        }
    }

}
