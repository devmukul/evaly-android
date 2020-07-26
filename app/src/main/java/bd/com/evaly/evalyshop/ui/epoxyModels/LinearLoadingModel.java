package bd.com.evaly.evalyshop.ui.epoxyModels;

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
public abstract class LinearLoadingModel extends EpoxyModelWithHolder<LinearLoadingModel.Holder> {
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
            container = itemView.findViewById(R.id.container);
        }
    }

}
