package bd.com.evaly.evalyshop.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bd.com.evaly.evalyshop.listener.OtpReceiverListener;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static OtpReceiverListener otpReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    Log.e("OtpMessage", "otp: "+message);
                    retrieveOtpFromMessage(message);

                    break;
                case CommonStatusCodes.TIMEOUT:
                    break;
            }
        }
    }

    private void retrieveOtpFromMessage(String message) {
        if (message != null) {
            Pattern pattern = Pattern.compile("(\\d{5})");
            //   \d is for a digit
            //   {} is the number of digits here 5.
            Matcher matcher = pattern.matcher(message);
            String otp = "";
            if (matcher.find()) {
                otp = matcher.group(0);  // 5 digit number
                Log.e("retrieveOtp", "myotp: "+otp);
                if (otpReceiverListener != null)
                    otpReceiverListener.onOtpReceive(otp);
            }
        }
    }
}
