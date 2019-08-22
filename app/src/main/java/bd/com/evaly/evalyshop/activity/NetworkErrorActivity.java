package bd.com.evaly.evalyshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import bd.com.evaly.evalyshop.R;

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
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
