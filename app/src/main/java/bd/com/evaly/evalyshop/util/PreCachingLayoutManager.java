package bd.com.evaly.evalyshop.util;

import android.content.Context;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class PreCachingLayoutManager extends StaggeredGridLayoutManager {
    private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 600;
    private int extraLayoutSpace = -1;
    private Context context;


    public PreCachingLayoutManager(int spanCount, int orientation, int extraLayoutSpace) {
        super(spanCount, orientation);
        this.extraLayoutSpace = extraLayoutSpace;
    }

    public void setExtraLayoutSpace(int extraLayoutSpace) {
        this.extraLayoutSpace = extraLayoutSpace;
    }


}
