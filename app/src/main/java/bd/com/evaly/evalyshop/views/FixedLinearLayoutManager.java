package bd.com.evaly.evalyshop.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;

public class FixedLinearLayoutManager extends LinearLayoutManager {


    public FixedLinearLayoutManager(Context context) {
        super(context);
    }

    public FixedLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public FixedLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            Logger.e("hmtzz" + e.toString());
        }
    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        try {
            super.onItemsAdded(recyclerView, positionStart, itemCount);
        } catch (Exception e) {
            Logger.e("hmtzz" + e.toString());
        }
    }
}
