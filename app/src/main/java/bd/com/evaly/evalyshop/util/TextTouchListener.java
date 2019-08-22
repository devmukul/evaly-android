package bd.com.evaly.evalyshop.util;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class TextTouchListener implements View.OnTouchListener {
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                ((TextView)view).setTextColor(Color.parseColor("#e06d0f")); //white
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ((TextView)view).setTextColor(Color.parseColor("#333333")); //black
                break;
        }
        return false;
    }
}