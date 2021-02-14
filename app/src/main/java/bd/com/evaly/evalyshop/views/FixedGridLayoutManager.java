package bd.com.evaly.evalyshop.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;

public class FixedGridLayoutManager extends GridLayoutManager {

    public FixedGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FixedGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public FixedGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
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
