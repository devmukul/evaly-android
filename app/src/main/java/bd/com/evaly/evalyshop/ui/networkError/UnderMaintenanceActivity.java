package bd.com.evaly.evalyshop.ui.networkError;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import bd.com.evaly.evalyshop.R;

public class UnderMaintenanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_maintenance);

        Button button = findViewById(R.id.button);
        TextView textView = findViewById(R.id.text);

        if (getIntent() != null && getIntent().hasExtra("text") && getIntent().getStringExtra("text") != null && !getIntent().getStringExtra("text").equals("")){
            textView.setText(Objects.requireNonNull(getIntent().getStringExtra("text")).replace("\\n", "\n"));
        }

        button.setOnClickListener(v -> {
            finishAffinity();
        });
    }
}
