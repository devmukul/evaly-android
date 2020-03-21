package bd.com.evaly.evalyshop.epoxy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyControllerAdapter;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.epoxy.controller.HeaderController;
import bd.com.evaly.evalyshop.epoxy.decoration.GridSpacingDecoration;
import bd.com.evaly.evalyshop.util.Utils;

public class EpoxyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epoxy);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        HeaderController controller = new HeaderController();
        recyclerView.setAdapter(controller.getAdapter());

        EpoxyControllerAdapter adapter = controller.getAdapter();

        int spanCount = 2;
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        controller.setSpanCount(spanCount);

        int spacing = (int) Utils.convertDpToPixel(10, this);
        recyclerView.addItemDecoration(new GridSpacingDecoration(spanCount, spacing, true));

        recyclerView.setLayoutManager(layoutManager);

        controller.setData(new ArrayList<>(), false);

    }
}
