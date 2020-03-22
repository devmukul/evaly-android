package bd.com.evaly.evalyshop.epoxy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.List;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.epoxy.controller.HeaderController;
import bd.com.evaly.evalyshop.epoxy.decoration.GridSpacingDecoration;
import bd.com.evaly.evalyshop.listener.ResponseListenerAuth;
import bd.com.evaly.evalyshop.models.CommonResultResponse;
import bd.com.evaly.evalyshop.models.product.ProductItem;
import bd.com.evaly.evalyshop.rest.apiHelper.ProductApiHelper;
import bd.com.evaly.evalyshop.util.Utils;

public class EpoxyActivity extends AppCompatActivity {

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private boolean isLoading = false;
    private HeaderController controller;
    private int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epoxy);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        controller = new HeaderController();
        controller.setActivity(this);

        recyclerView.setAdapter(controller.getAdapter());

        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        controller.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, this);
        recyclerView.addItemDecoration(new GridSpacingDecoration(spanCount, spacing, true));

        recyclerView.setLayoutManager(layoutManager);

        loadProducts(currentPage);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    int[] firstVisibleItems = null;
                    firstVisibleItems = layoutManager.findFirstVisibleItemPositions(null);
                    if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                        pastVisiblesItems = firstVisibleItems[0];
                    }

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loadProducts(++currentPage);
                        }
                    }
                }
            }
        });

    }


    private void loadProducts(int page) {

        controller.setLoadingMore(true);

        ProductApiHelper.getCategoryBrandProducts(page, null, null, new ResponseListenerAuth<CommonResultResponse<List<ProductItem>>, String>() {
            @Override
            public void onDataFetched(CommonResultResponse<List<ProductItem>> response, int statusCode) {
                controller.setLoadingMore(false);
                controller.addData(response.getData());
            }

            @Override
            public void onFailed(String errorBody, int errorCode) {

            }

            @Override
            public void onAuthError(boolean logout) {

            }
        });
    }
}
