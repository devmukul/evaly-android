package bd.com.evaly.evalyshop.activity.newsfeed;

import android.app.Dialog;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.ScreenUtils;

public class NewsfeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Newsfeed");







        BottomSheetDialog dialog = new BottomSheetDialog(NewsfeedActivity.this, R.style.BottomSheetDialogTheme);
        dialog.setContentView(R.layout.alert_comments);

        View bottomSheetInternal = dialog.findViewById(android.support.design.R.id.design_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetInternal);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        ScreenUtils screenUtils = new ScreenUtils(NewsfeedActivity.this);
//                bottomSheetBehavior.setPeekHeight(15000);

        LinearLayout dialogLayout = dialog.findViewById(R.id.container2);
        dialogLayout.setMinimumHeight(screenUtils.getHeight());




    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
