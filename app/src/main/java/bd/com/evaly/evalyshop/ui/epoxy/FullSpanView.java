package bd.com.evaly.evalyshop.ui.epoxy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.orhanobut.logger.Logger;

public class FullSpanView extends View {
    public FullSpanView(Context context) {
        super(context);

        Logger.e("view called");
    }

    public FullSpanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FullSpanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FullSpanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public void setLayoutParams(ViewGroup.LayoutParams paramsz) {
        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setFullSpan(true);
    }
}
