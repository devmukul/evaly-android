package bd.com.evaly.evalyshop.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewNoLeak extends RecyclerView {
    public RecyclerViewNoLeak(@NonNull Context context) {
        super(context);
    }

    public RecyclerViewNoLeak(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewNoLeak(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (getAdapter() != null) {
            setAdapter(null);
        }
    }
}

