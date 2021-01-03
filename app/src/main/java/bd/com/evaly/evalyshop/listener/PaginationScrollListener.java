package bd.com.evaly.evalyshop.listener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;

    public PaginationScrollListener() {

    }

    public PaginationScrollListener(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    public PaginationScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    public PaginationScrollListener(StaggeredGridLayoutManager staggeredGridLayoutManager) {
        this.staggeredGridLayoutManager = staggeredGridLayoutManager;
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
        this.gridLayoutManager = null;
        this.staggeredGridLayoutManager = null;
    }

    public void setGridLayoutManager(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
        this.linearLayoutManager = null;
        this.staggeredGridLayoutManager = null;
    }

    public void setStaggeredGridLayoutManager(StaggeredGridLayoutManager staggeredGridLayoutManager) {
        this.gridLayoutManager = null;
        this.linearLayoutManager = null;
        this.staggeredGridLayoutManager = staggeredGridLayoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0) {
            int visibleItemCount = 0, totalItemCount = 0, firstVisibleItemPosition = 0;
            if (gridLayoutManager != null) {
                visibleItemCount = gridLayoutManager.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount();
                firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
            }
            if (linearLayoutManager != null) {
                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            }

            if (staggeredGridLayoutManager != null) {
                visibleItemCount = staggeredGridLayoutManager.getChildCount();
                totalItemCount = staggeredGridLayoutManager.getItemCount();
                int[] firstVisibleItems;
                firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(null);
                if (firstVisibleItems != null && firstVisibleItems.length > 0)
                    firstVisibleItemPosition = firstVisibleItems[0];
            }

            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0)
                loadMoreItem();
        }
    }

    public abstract void loadMoreItem();

}
