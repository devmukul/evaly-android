package bd.com.evaly.evalyshop.views;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

public class FixedStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

    public FixedStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
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
    public void invalidateSpanAssignments() {
        try {
            super.invalidateSpanAssignments();
        } catch (Exception e) {
            Logger.e("hmtzz" + e.toString());
        }
    }

//
//    @Override
//    public boolean supportsPredictiveItemAnimations() {
//        return false;
//    }

    @Override
    public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
        try {
            super.onItemsAdded(recyclerView, positionStart, itemCount);
        } catch (Exception e) {
            Logger.e("hmtzz startPos: " + positionStart + " itemCount: " + itemCount + " " + e.toString() + new Gson().toJson(e));
        }
    }
}
