package bd.com.evaly.evalyshop.epoxy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.epoxy.controller.HeaderController;

public class EpoxyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epoxy);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        HeaderController controller = new HeaderController();
        recyclerView.setAdapter(controller.getAdapter());


        controller.setData(new ArrayList<>(), false);

    }
}
