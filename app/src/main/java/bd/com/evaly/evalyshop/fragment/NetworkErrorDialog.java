package bd.com.evaly.evalyshop.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import bd.com.evaly.evalyshop.R;
import bd.com.evaly.evalyshop.util.Utils;

public class NetworkErrorDialog extends Dialog {

    private NetworkErrorDialogListener listener;

    public NetworkErrorDialog(@NonNull Context context, NetworkErrorDialogListener listener) {
        super(context, android.R.style.Theme_Material_Light_NoActionBar);
        this.setContentView(R.layout.activity_network_error);
        this.listener = listener;

        if (this.getWindow() != null)
            this.getWindow().setStatusBarColor(context.getResources().getColor(R.color.white));

        Button button = findViewById(R.id.button);

        // this.setCancelable(false);

        button.setOnClickListener(view -> {
            if (Utils.isNetworkAvailable(context)) {
                this.dismiss();
                listener.onRetry();
            }
            else
                Toast.makeText(context,"Please turn on mobile data or Wi-Fi", Toast.LENGTH_SHORT).show();
        });

        this.setOnCancelListener((DialogInterface dialogInterface) -> {
            listener.onBackPress();
        });

        this.show();

    }


}
