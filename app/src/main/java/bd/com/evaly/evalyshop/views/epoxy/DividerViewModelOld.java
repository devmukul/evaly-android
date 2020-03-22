package bd.com.evaly.evalyshop.views.epoxy;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.epoxy.ModelView;

import bd.com.evaly.evalyshop.R;

@ModelView(defaultLayout = R.layout.divider_view)
public class DividerViewModelOld extends View {

    public DividerViewModelOld(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void setMargin(int left, int top, int right, int bottom){
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
        params.setMargins(left, top, right, bottom);
        requestLayout();
    }

}
