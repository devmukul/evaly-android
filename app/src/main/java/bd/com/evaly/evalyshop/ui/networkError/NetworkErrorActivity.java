package bd.com.evaly.evalyshop.ui.networkError;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.ui.main.MainActivity;
import bd.com.evaly.evalyshop.util.Utils;

public class NetworkErrorActivity extends AppCompatActivity {


    /*
        H. M. Tamim
        24/Jun/2019
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_error);


        Button button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            if (Utils.isNetworkAvailable(this)) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                Toast.makeText(this, "Turn on mobile data or Wi-Fi", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed(){

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

        return;

    }
}
