package bd.com.evaly.evalyshop.ui.home.model;

import android.view.View;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;

import bd.com.evaly.evalyshop.R;

@EpoxyModelClass(layout = R.layout.home_model_express_service_skeleton)
public abstract class HomeExpressServiceSkeletonModel extends EpoxyModelWithHolder<HomeExpressServiceSkeletonModel.Holder> {

    class Holder extends EpoxyHolder {
        View itemView;

        @Override
        protected void bindView(@NonNull View itemView) {
            this.itemView = itemView;
//            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
//            params.setFullSpan(true);
        }
    }

}
