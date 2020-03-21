package bd.com.evaly.evalyshop.epoxy.models;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyModel;

import bd.com.evaly.evalyshop.R;


public class NoHolderModel extends EpoxyModel<ConstraintLayout> {

    public NoHolderModel() {
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.epoxy_no_holder_item;
    }

    @Override
    public void bind(@NonNull ConstraintLayout view) {
        super.bind(view);

        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        params.setFullSpan(true);

        TextView textView = view.findViewById(R.id.text);
        textView.setText("HMMMMM");
    }
}
